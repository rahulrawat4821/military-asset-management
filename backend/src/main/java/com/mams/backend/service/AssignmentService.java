package com.mams.backend.service;

import com.mams.backend.audit.AuditLogService;
import com.mams.backend.dto.AssignmentRequest;
import com.mams.backend.dto.AssignmentResponse;
import com.mams.backend.model.*;
import com.mams.backend.model.enums.AssignmentStatus;
import com.mams.backend.model.enums.Role;
import com.mams.backend.repository.AssetRepository;
import com.mams.backend.repository.AssignmentRepository;
import com.mams.backend.repository.BaseRepository;
import com.mams.backend.repository.EquipmentTypeRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final AssetRepository assetRepository;
    private final AuditLogService auditLogService;

    public AssignmentService(AssignmentRepository assignmentRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, AssetRepository assetRepository, AuditLogService auditLogService) {
        this.assignmentRepository = assignmentRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.assetRepository = assetRepository;
        this.auditLogService = auditLogService;
    }


    // NOTE on the accounting model (worth restating in the PDF write-up):
    // an asset's quantity is decremented from the base's on-hand stock as soon as
    // it's assigned to personnel - not when it's later marked expended. "Expended"
    // is just a status change on the same row (the asset already left inventory
    // the moment it was assigned out).
    @Transactional
    public AssignmentResponse create(AssignmentRequest request, User currentUser) {
        com.mams.backend.model.Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Base not found: " + request.getBaseId()));

        if (currentUser.getRole() != Role.ADMIN
                && (currentUser.getBase() == null || !currentUser.getBase().getId().equals(base.getId()))) {
            throw new IllegalArgumentException("You can only assign assets for your own base");
        }

        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment type not found: " + request.getEquipmentTypeId()));

        Asset asset = assetRepository.findByBaseIdAndEquipmentTypeId(base.getId(), equipmentType.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        base.getName() + " has no stock of " + equipmentType.getName()));

        if (asset.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock at " + base.getName() + ": has " + asset.getQuantity()
                            + ", tried to assign " + request.getQuantity());
        }

        asset.setQuantity(asset.getQuantity() - request.getQuantity());
        assetRepository.save(asset);

        Assignment assignment = new Assignment();
        assignment.setBase(base);
        assignment.setEquipmentType(equipmentType);
        assignment.setPersonnelName(request.getPersonnelName());
        assignment.setQuantity(request.getQuantity());
        assignment.setAssignmentDate(request.getAssignmentDate());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setCreatedBy(currentUser);
        assignment = assignmentRepository.save(assignment);

        auditLogService.log("CREATE_ASSIGNMENT", "Assignment", assignment.getId(),
                "base=" + base.getName() + ", equipment=" + equipmentType.getName()
                        + ", qty=" + request.getQuantity() + ", personnel=" + request.getPersonnelName());

        return toResponse(assignment);
    }

    @Transactional
    public AssignmentResponse markExpended(Long assignmentId, User currentUser) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        if (currentUser.getRole() != Role.ADMIN
                && (currentUser.getBase() == null || !currentUser.getBase().getId().equals(assignment.getBase().getId()))) {
            throw new IllegalArgumentException("You can only update assignments for your own base");
        }

        if (assignment.getStatus() == AssignmentStatus.EXPENDED) {
            throw new IllegalArgumentException("Assignment is already marked as expended");
        }

        assignment.setStatus(AssignmentStatus.EXPENDED);
        assignment = assignmentRepository.save(assignment);

        auditLogService.log("EXPEND_ASSIGNMENT", "Assignment", assignment.getId(),
                "personnel=" + assignment.getPersonnelName() + ", qty=" + assignment.getQuantity());

        return toResponse(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponse> list(Long baseId, Long equipmentTypeId, String status,
                                          LocalDate fromDate, LocalDate toDate, User currentUser) {
        Long effectiveBaseId = baseId;
        if (currentUser.getRole() != Role.ADMIN) {
            effectiveBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
        }

        Specification<Assignment> spec = buildSpec(effectiveBaseId, equipmentTypeId, status, fromDate, toDate);

        return assignmentRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    private Specification<Assignment> buildSpec(Long baseId, Long equipmentTypeId, String status,
                                                 LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (baseId != null) {
                predicates.add(cb.equal(root.get("base").get("id"), baseId));
            }
            if (equipmentTypeId != null) {
                predicates.add(cb.equal(root.get("equipmentType").get("id"), equipmentTypeId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), AssignmentStatus.valueOf(status.toUpperCase())));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("assignmentDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("assignmentDate"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private AssignmentResponse toResponse(Assignment a) {
        return new AssignmentResponse(
                a.getId(),
                a.getBase().getId(),
                a.getBase().getName(),
                a.getEquipmentType().getId(),
                a.getEquipmentType().getName(),
                a.getPersonnelName(),
                a.getQuantity(),
                a.getAssignmentDate(),
                a.getStatus().name(),
                a.getCreatedBy() != null ? a.getCreatedBy().getUsername() : null,
                a.getCreatedAt()
        );
    }
}
