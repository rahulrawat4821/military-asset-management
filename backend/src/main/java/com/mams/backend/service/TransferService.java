package com.mams.backend.service;

import com.mams.backend.audit.AuditLogService;
import com.mams.backend.dto.TransferRequest;
import com.mams.backend.dto.TransferResponse;
import com.mams.backend.model.*;
import com.mams.backend.model.enums.Role;
import com.mams.backend.model.enums.TransferStatus;
import com.mams.backend.repository.AssetRepository;
import com.mams.backend.repository.BaseRepository;
import com.mams.backend.repository.EquipmentTypeRepository;
import com.mams.backend.repository.TransferRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final AssetRepository assetRepository;
    private final AuditLogService auditLogService;

    public TransferService(TransferRepository transferRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, AssetRepository assetRepository, AuditLogService auditLogService) {
        this.transferRepository = transferRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.assetRepository = assetRepository;
        this.auditLogService = auditLogService;
    }


    @Transactional
    public TransferResponse create(TransferRequest request, User currentUser) {
        if (request.getFromBaseId().equals(request.getToBaseId())) {
            throw new IllegalArgumentException("fromBaseId and toBaseId cannot be the same base");
        }

        com.mams.backend.model.Base fromBase = baseRepository.findById(request.getFromBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Base not found: " + request.getFromBaseId()));
        com.mams.backend.model.Base toBase = baseRepository.findById(request.getToBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Base not found: " + request.getToBaseId()));

        // commander / logistics officer can only move stock out of (or into) their own base
        if (currentUser.getRole() != Role.ADMIN) {
            Long ownBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : null;
            boolean involvesOwnBase = ownBaseId != null
                    && (ownBaseId.equals(fromBase.getId()) || ownBaseId.equals(toBase.getId()));
            if (!involvesOwnBase) {
                throw new IllegalArgumentException("You can only transfer assets involving your own base");
            }
        }

        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment type not found: " + request.getEquipmentTypeId()));

        Asset fromAsset = assetRepository.findByBaseIdAndEquipmentTypeId(fromBase.getId(), equipmentType.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        fromBase.getName() + " has no stock of " + equipmentType.getName()));

        if (fromAsset.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock at " + fromBase.getName() + ": has " + fromAsset.getQuantity()
                            + ", tried to transfer " + request.getQuantity());
        }

        fromAsset.setQuantity(fromAsset.getQuantity() - request.getQuantity());
        assetRepository.save(fromAsset);

        Asset toAsset = assetRepository.findByBaseIdAndEquipmentTypeId(toBase.getId(), equipmentType.getId())
                .orElseGet(() -> new Asset(null, toBase, equipmentType, 0));
        toAsset.setQuantity(toAsset.getQuantity() + request.getQuantity());
        assetRepository.save(toAsset);

        Transfer transfer = new Transfer();
        transfer.setFromBase(fromBase);
        transfer.setToBase(toBase);
        transfer.setEquipmentType(equipmentType);
        transfer.setQuantity(request.getQuantity());
        transfer.setTransferDate(request.getTransferDate());
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCreatedBy(currentUser);
        transfer = transferRepository.save(transfer);

        auditLogService.log("CREATE_TRANSFER", "Transfer", transfer.getId(),
                "from=" + fromBase.getName() + ", to=" + toBase.getName()
                        + ", equipment=" + equipmentType.getName() + ", qty=" + request.getQuantity());

        return toResponse(transfer);
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> list(Long baseId, Long equipmentTypeId, LocalDate fromDate, LocalDate toDate, User currentUser) {
        Long ownBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : null;
        boolean restrictToOwnBase = currentUser.getRole() != Role.ADMIN;

        Specification<Transfer> spec = buildSpec(baseId, equipmentTypeId, fromDate, toDate, restrictToOwnBase, ownBaseId);

        return transferRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    private Specification<Transfer> buildSpec(Long baseId, Long equipmentTypeId, LocalDate fromDate, LocalDate toDate,
                                               boolean restrictToOwnBase, Long ownBaseId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (restrictToOwnBase) {
                Long scopeId = ownBaseId != null ? ownBaseId : -1L;
                predicates.add(cb.or(
                        cb.equal(root.get("fromBase").get("id"), scopeId),
                        cb.equal(root.get("toBase").get("id"), scopeId)
                ));
            } else if (baseId != null) {
                predicates.add(cb.or(
                        cb.equal(root.get("fromBase").get("id"), baseId),
                        cb.equal(root.get("toBase").get("id"), baseId)
                ));
            }

            if (equipmentTypeId != null) {
                predicates.add(cb.equal(root.get("equipmentType").get("id"), equipmentTypeId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transferDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transferDate"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private TransferResponse toResponse(Transfer t) {
        return new TransferResponse(
                t.getId(),
                t.getFromBase().getId(),
                t.getFromBase().getName(),
                t.getToBase().getId(),
                t.getToBase().getName(),
                t.getEquipmentType().getId(),
                t.getEquipmentType().getName(),
                t.getQuantity(),
                t.getTransferDate(),
                t.getStatus().name(),
                t.getCreatedBy() != null ? t.getCreatedBy().getUsername() : null,
                t.getCreatedAt()
        );
    }
}
