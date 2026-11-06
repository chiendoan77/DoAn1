package com.doan1.catalog.service;

import com.doan1.catalog.dto.request.CreatePackageRequest;
import com.doan1.catalog.dto.request.RejectPackageRequest;
import com.doan1.catalog.dto.request.UpdatePackageRequest;
import com.doan1.catalog.dto.response.HistoryResponse;
import com.doan1.catalog.dto.response.PackageResponse;
import com.doan1.catalog.dto.response.PageResponse;
import com.doan1.catalog.entity.Category;
import com.doan1.catalog.entity.OutboxEvent;
import com.doan1.catalog.entity.PackageHistory;
import com.doan1.catalog.entity.ServicePackage;
import com.doan1.catalog.enums.HistoryAction;
import com.doan1.catalog.enums.PackageStatus;
import com.doan1.catalog.repository.CategoryRepository;
import com.doan1.catalog.repository.OutboxEventRepository;
import com.doan1.catalog.repository.PackageHistoryRepository;
import com.doan1.catalog.repository.ServicePackageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PackageWorkflowService {
    private final ServicePackageRepository packages;
    private final CategoryRepository categories;
    private final PackageHistoryRepository history;
    private final OutboxEventRepository outbox;
    private final ObjectMapper objectMapper;

    public PackageWorkflowService(
            ServicePackageRepository packages,
            CategoryRepository categories,
            PackageHistoryRepository history,
            OutboxEventRepository outbox,
            ObjectMapper objectMapper) {
        this.packages = packages;
        this.categories = categories;
        this.history = history;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
    }

    private boolean isAdminAction(HistoryAction action) {
        return action == HistoryAction.APPROVED
                || action == HistoryAction.REJECTED
                || action == HistoryAction.DEACTIVATED;
    }

    @Transactional
    public PackageResponse create(UUID accountId, UUID providerId, CreatePackageRequest request) {
        Category category = activeCategory(request.categoryId());
        ServicePackage servicePackage = new ServicePackage(
                accountId, providerId, category, request.name().trim(), request.description().trim(),
                request.price(), request.durationMinutes(), request.providerPhone());
        packages.saveAndFlush(servicePackage);
        record(servicePackage, accountId, HistoryAction.CREATED, null);
        return response(servicePackage);
    }

    @Transactional(readOnly = true)
    public PageResponse<PackageResponse> providerPackages(
            UUID accountId, int page, int size, String sort, String direction) {
        Specification<ServicePackage> specification = (root, query, builder) ->
                builder.equal(root.get("ownerAccountId"), accountId);
        return page(packages.findAll(specification, PageableFactory.create(page, size, sort, direction)));
    }

    @Transactional(readOnly = true)
    public PackageResponse providerPackage(UUID accountId, UUID packageId) {
        return packages.findByIdAndOwnerAccountId(packageId, accountId)
                .map(this::response)
                .orElseThrow(this::notFound);
    }

    @Transactional
    public PackageResponse update(
            UUID accountId, UUID currentProviderId, UUID packageId, UpdatePackageRequest request) {
        ServicePackage servicePackage = ownedPackage(packageId, accountId);
        if (currentProviderId != null) {
            requireSameProvider(servicePackage, currentProviderId);
        }
        requireVersion(servicePackage, request.expectedVersion());
        if (servicePackage.getStatus() == PackageStatus.PENDING_APPROVAL) {
            throw conflict("Packages awaiting approval cannot be edited.");
        }
        PackageStatus previousStatus = servicePackage.getStatus();
        Category category = activeCategory(request.categoryId());
        servicePackage.updateContent(
                category, request.name().trim(), request.description().trim(), request.price(),
                request.durationMinutes(), request.providerPhone());
        packages.saveAndFlush(servicePackage);
        HistoryAction action = switch (previousStatus) {
            case REJECTED -> HistoryAction.EDITED_AFTER_REJECTION;
            case INACTIVE -> HistoryAction.EDITED_AFTER_INACTIVATION;
            case APPROVED -> HistoryAction.RESUBMITTED;
            default -> HistoryAction.UPDATED;
        };
        record(servicePackage, accountId, action, null);
        if (previousStatus == PackageStatus.APPROVED) {
            writeEvent("PackageResubmitted", servicePackage, null, null);
        }
        return response(servicePackage);
    }

    @Transactional
    public void delete(UUID accountId, UUID packageId, long expectedVersion) {
        ServicePackage servicePackage = ownedPackage(packageId, accountId);
        requireVersion(servicePackage, expectedVersion);
        if (servicePackage.getStatus() != PackageStatus.DRAFT
                && servicePackage.getStatus() != PackageStatus.REJECTED) {
            throw conflict("Only draft or rejected packages can be deleted.");
        }
        record(servicePackage, accountId, HistoryAction.DELETED, null);
        packages.delete(servicePackage);
        packages.flush();
    }

    @Transactional
    public PackageResponse submit(UUID accountId, UUID currentProviderId, UUID packageId, long expectedVersion) {
        ServicePackage servicePackage = ownedPackage(packageId, accountId);
        requireSameProvider(servicePackage, currentProviderId);
        requireVersion(servicePackage, expectedVersion);
        if (servicePackage.getStatus() != PackageStatus.DRAFT) {
            throw conflict("Only draft packages can be submitted.");
        }
        boolean resubmission = servicePackage.hasBeenSubmitted();
        servicePackage.submit();
        packages.saveAndFlush(servicePackage);
        HistoryAction action = resubmission ? HistoryAction.RESUBMITTED : HistoryAction.SUBMITTED;
        record(servicePackage, accountId, action, null);
        writeEvent(resubmission ? "PackageResubmitted" : "PackageSubmitted", servicePackage, null, null);
        return response(servicePackage);
    }

    @Transactional(readOnly = true)
    public PageResponse<PackageResponse> pendingPackages(
            int page, int size, String sort, String direction) {
        return page(search(PackageStatus.PENDING_APPROVAL, null, null, null, null, null,
                PageableFactory.create(page, size, sort, direction)));
    }

    @Transactional(readOnly = true)
    public PageResponse<PackageResponse> adminPackages(
            PackageStatus status,
            String categoryId,
            UUID providerId,
            String query,
            int page,
            int size,
            String sort,
            String direction) {
        return page(search(status, categoryId, providerId, query, null, null,
                PageableFactory.create(page, size, sort, direction)));
    }

    @Transactional(readOnly = true)
    public PackageResponse adminPackage(UUID packageId) {
        return packages.findById(packageId).map(this::response).orElseThrow(this::notFound);
    }

    @Transactional
    public PackageResponse approve(UUID adminId, UUID packageId, long expectedVersion) {
        ServicePackage servicePackage = pendingPackage(packageId, expectedVersion);
        servicePackage.approve();
        packages.saveAndFlush(servicePackage);
        record(servicePackage, adminId, HistoryAction.APPROVED, null);
        writeEvent("PackageApproved", servicePackage, adminId, null);
        return response(servicePackage);
    }

    @Transactional
    public PackageResponse reject(UUID adminId, UUID packageId, String reason, long expectedVersion) {
        ServicePackage servicePackage = pendingPackage(packageId, expectedVersion);
        reason = reason.trim();
        servicePackage.reject(reason);
        packages.saveAndFlush(servicePackage);
        record(servicePackage, adminId, HistoryAction.REJECTED, reason);
        writeEvent("PackageRejected", servicePackage, adminId, reason);
        return response(servicePackage);
    }

    @Transactional(readOnly = true)
    public List<HistoryResponse> history(UUID packageId) {
        requireExists(packageId);
        return history.findAllByPackageIdOrderByCreatedAtAsc(packageId).stream()
                .map(item -> new HistoryResponse(
                        item.getId(), item.getPackageId(), item.getActorId(),
                        isAdminAction(item.getAction()) ? item.getActorId() : null, item.getAction(),
                        item.getReason(), item.getPackageVersion(), item.getCreatedAt()))
                .toList();
    }

    @Transactional
    public PackageResponse deactivate(UUID adminId, UUID packageId, long expectedVersion) {
        ServicePackage servicePackage = packages.findById(packageId).orElseThrow(this::notFound);
        requireVersion(servicePackage, expectedVersion);
        if (servicePackage.getStatus() != PackageStatus.APPROVED) {
            throw conflict("Only approved packages can be deactivated.");
        }
        servicePackage.deactivate();
        packages.saveAndFlush(servicePackage);
        record(servicePackage, adminId, HistoryAction.DEACTIVATED, null);
        writeEvent("PackageDeactivated", servicePackage, adminId, null);
        return response(servicePackage);
    }

    @Transactional(readOnly = true)
    public PageResponse<PackageResponse> publicPackages(
            String categoryId,
            String query,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sort,
            String direction) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPrice cannot exceed maxPrice.");
        }
        Pageable pageable = PageableFactory.create(page, size, sort, direction);
        return page(search(PackageStatus.APPROVED, categoryId, null, query, minPrice, maxPrice, pageable));
    }

    @Transactional(readOnly = true)
    public PackageResponse publicPackage(UUID packageId) {
        return packages.findById(packageId)
                .filter(item -> item.getStatus() == PackageStatus.APPROVED)
                .map(this::response)
                .orElseThrow(this::notFound);
    }

    private Page<ServicePackage> search(
            PackageStatus status,
            String categoryId,
            UUID providerId,
            String query,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {
        Specification<ServicePackage> specification = (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (categoryId != null) {
                predicates.add(builder.equal(root.get("category").get("id"), categoryId));
            }
            if (providerId != null) {
                predicates.add(builder.equal(root.get("providerId"), providerId));
            }
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("name")), pattern),
                        builder.like(builder.lower(root.get("description")), pattern)));
            }
            if (minPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        return packages.findAll(specification, pageable);
    }

    private PageResponse<PackageResponse> page(Page<ServicePackage> page) {
        return new PageResponse<>(
                page.getContent().stream().map(this::response).toList(),
                page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    private PackageResponse response(ServicePackage servicePackage) {
        Category category = servicePackage.getCategory();
        return new PackageResponse(
                servicePackage.getId(), servicePackage.getProviderId(), category.getId(),
                servicePackage.getName(), servicePackage.getDescription(), servicePackage.getPrice(),
                servicePackage.getDurationMinutes(), servicePackage.getProviderPhone(), servicePackage.getStatus(),
                servicePackage.getRejectionReason(), servicePackage.getVersion(),
                servicePackage.getCreatedAt(), servicePackage.getUpdatedAt());
    }

    private Category activeCategory(String categoryId) {
        return categories.findById(categoryId)
                .filter(Category::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is unavailable."));
    }

    private ServicePackage ownedPackage(UUID packageId, UUID accountId) {
        return packages.findByIdAndOwnerAccountId(packageId, accountId).orElseThrow(this::notFound);
    }

    private ServicePackage pendingPackage(UUID packageId, long expectedVersion) {
        ServicePackage servicePackage = packages.findById(packageId).orElseThrow(this::notFound);
        requireVersion(servicePackage, expectedVersion);
        if (servicePackage.getStatus() != PackageStatus.PENDING_APPROVAL) {
            throw conflict("Only packages awaiting approval can be moderated.");
        }
        return servicePackage;
    }

    private void requireSameProvider(ServicePackage servicePackage, UUID providerId) {
        if (!servicePackage.getProviderId().equals(providerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Provider profile does not own this package.");
        }
    }

    private void requireVersion(ServicePackage servicePackage, long expectedVersion) {
        if (servicePackage.getVersion() != expectedVersion) {
            throw conflict("Package version is stale.");
        }
    }

    private void requireExists(UUID packageId) {
        if (!packages.existsById(packageId)) {
            throw notFound();
        }
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found.");
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    private void record(ServicePackage servicePackage, UUID actorId, HistoryAction action, String reason) {
        history.save(new PackageHistory(
                servicePackage.getId(), actorId, action, reason, servicePackage.getVersion()));
    }

    private void writeEvent(String eventType, ServicePackage servicePackage, UUID adminId, String reason) {
        UUID eventId = UUID.randomUUID();
        OutboxPayload payload = new OutboxPayload(
                eventId, eventType, servicePackage.getId(), servicePackage.getProviderId(),
                adminId, reason, Instant.now());
        try {
            outbox.save(new OutboxEvent(
                    eventId, servicePackage.getId(), eventType, objectMapper.writeValueAsString(payload),
                    "doan1.events", routingKey(eventType)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize the catalog outbox event.", exception);
        }
    }

    private String routingKey(String eventType) {
        String eventName = eventType.substring("Package".length()).toLowerCase(java.util.Locale.ROOT);
        return "catalog.package." + eventName;
    }

    private record OutboxPayload(
            UUID eventId,
            String eventType,
            UUID packageId,
            UUID providerId,
            UUID adminId,
            String reason,
            Instant occurredAt) {
    }
}
