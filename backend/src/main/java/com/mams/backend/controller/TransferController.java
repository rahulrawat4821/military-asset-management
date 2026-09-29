package com.mams.backend.controller;

import com.mams.backend.dto.TransferRequest;
import com.mams.backend.dto.TransferResponse;
import com.mams.backend.model.User;
import com.mams.backend.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<TransferResponse> create(@Valid @RequestBody TransferRequest request,
                                                    @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(transferService.create(request, currentUser));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<List<TransferResponse>> list(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(transferService.list(baseId, equipmentTypeId, fromDate, toDate, currentUser));
    }
}
