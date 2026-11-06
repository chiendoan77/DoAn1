package com.doan1.catalog.controller.admin;

import com.doan1.catalog.dto.request.RejectPackageRequest;
import com.doan1.catalog.dto.request.VersionRequest;
import com.doan1.catalog.dto.response.HistoryResponse;
import com.doan1.catalog.dto.response.PackageResponse;
import com.doan1.catalog.dto.response.PageResponse;
import com.doan1.catalog.enums.PackageStatus;
import com.doan1.catalog.security.JwtPrincipalService;
import com.doan1.catalog.service.PackageWorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/packages")
public class AdminPackageController {
    private final PackageWorkflowService workflow;
    private final JwtPrincipalService principals;

    public AdminPackageController(PackageWorkflowService workflow, JwtPrincipalService principals) {
        this.workflow = workflow;
        this.principals = principals;
    }

    @GetMapping
    public PageResponse<PackageResponse> list(
            HttpServletRequest request,
            @RequestParam(required = false) PackageStatus status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) UUID providerId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        principals.requireRole(request, "ADMIN");
        return workflow.adminPackages(status, categoryId, providerId, q, page, size, sort, direction);
    }

    @GetMapping("/pending")
    public PageResponse<PackageResponse> pending(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        principals.requireRole(request, "ADMIN");
        return workflow.pendingPackages(page, size, sort, direction);
    }

    @GetMapping("/{id}")
    public PackageResponse get(HttpServletRequest request, @PathVariable UUID id) {
        principals.requireRole(request, "ADMIN");
        return workflow.adminPackage(id);
    }

    @PostMapping("/{id}/approve")
    public PackageResponse approve(
            HttpServletRequest request,
            @PathVariable UUID id,
            @Valid @RequestBody VersionRequest body) {
        var principal = principals.requireRole(request, "ADMIN");
        return workflow.approve(principal.accountId(), id, body.expectedVersion());
    }

    @PostMapping("/{id}/reject")
    public PackageResponse reject(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestHeader("If-Match") long expectedVersion,
            @Valid @RequestBody RejectPackageRequest body) {
        var principal = principals.requireRole(request, "ADMIN");
        return workflow.reject(principal.accountId(), id, body.reason(), expectedVersion);
    }

    @GetMapping("/{id}/history")
    public List<HistoryResponse> history(HttpServletRequest request, @PathVariable UUID id) {
        principals.requireRole(request, "ADMIN");
        return workflow.history(id);
    }

    @PostMapping("/{id}/deactivate")
    public PackageResponse deactivate(
            HttpServletRequest request,
            @PathVariable UUID id,
            @Valid @RequestBody VersionRequest body) {
        var principal = principals.requireRole(request, "ADMIN");
        return workflow.deactivate(principal.accountId(), id, body.expectedVersion());
    }
}
