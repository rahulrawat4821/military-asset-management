package com.mams.backend.controller;

import com.mams.backend.dto.AssignmentRequest;
import com.mams.backend.dto.AssignmentResponse;
import com.mams.backend.model.User;
import com.mams.backend.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Logistics Officer is deliberately left out of every mapping here - the spec
// scopes that role to purchases and transfers only.
@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<AssignmentResponse> create(@Valid @RequestBody AssignmentRequest request,
                                                      @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(assignmentService.create(request, currentUser));
    }

    @PatchMapping("/{id}/expend")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<AssignmentResponse> expend(@PathVariable Long id,
                                                      @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(assignmentService.markExpended(id, currentUser));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<List<AssignmentResponse>> list(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(assignmentService.list(baseId, equipmentTypeId, status, fromDate, toDate, currentUser));
    }
}
