package com.doan1.catalog.controller.provider;

import com.doan1.catalog.dto.request.CreatePackageRequest;
import com.doan1.catalog.dto.request.UpdatePackageRequest;
import com.doan1.catalog.dto.request.VersionRequest;
import com.doan1.catalog.dto.response.PackageResponse;
import com.doan1.catalog.dto.response.PageResponse;
import com.doan1.catalog.security.JwtPrincipalService;
import com.doan1.catalog.service.ProviderPackageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/provider/packages")
public class ProviderPackageController {
    private final ProviderPackageService providerPackages;
    private final JwtPrincipalService principals;

    public ProviderPackageController(ProviderPackageService providerPackages, JwtPrincipalService principals) {
        this.providerPackages = providerPackages;
        this.principals = principals;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PackageResponse create(
            HttpServletRequest request, @Valid @RequestBody CreatePackageRequest body) {
        var principal = principals.requireRole(request, "PROVIDER");
        return providerPackages.create(principal.accountId(), body);
    }

    @GetMapping({"", "/my"})
    public PageResponse<PackageResponse> list(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        var principal = principals.requireRole(request, "PROVIDER");
        return providerPackages.list(principal.accountId(), page, size, sort, direction);
    }

    @GetMapping("/{id}")
    public PackageResponse get(HttpServletRequest request, @PathVariable UUID id) {
        var principal = principals.requireRole(request, "PROVIDER");
        return providerPackages.get(principal.accountId(), id);
    }

    @PutMapping("/{id}")
    public PackageResponse update(
            HttpServletRequest request,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePackageRequest body) {
        var principal = principals.requireRole(request, "PROVIDER");
        return providerPackages.update(principal.accountId(), id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestParam long expectedVersion) {
        var principal = principals.requireRole(request, "PROVIDER");
        providerPackages.delete(principal.accountId(), id, expectedVersion);
    }

    @PostMapping("/{id}/submit")
    public PackageResponse submit(
            HttpServletRequest request,
            @PathVariable UUID id,
            @Valid @RequestBody VersionRequest body) {
        var principal = principals.requireRole(request, "PROVIDER");
        return providerPackages.submit(principal.accountId(), id, body.expectedVersion());
    }
}
