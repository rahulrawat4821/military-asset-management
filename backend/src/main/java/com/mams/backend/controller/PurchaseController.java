package com.mams.backend.controller;

import com.mams.backend.dto.PurchaseRequest;
import com.mams.backend.dto.PurchaseResponse;
import com.mams.backend.model.User;
import com.mams.backend.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<PurchaseResponse> create(@Valid @RequestBody PurchaseRequest request,
                                                    @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(purchaseService.create(request, currentUser));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<List<PurchaseResponse>> list(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(purchaseService.list(baseId, equipmentTypeId, fromDate, toDate, currentUser));
    }
}
