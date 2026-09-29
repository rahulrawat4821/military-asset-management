package com.mams.backend.service;

import com.mams.backend.audit.AuditLogService;
import com.mams.backend.dto.PurchaseRequest;
import com.mams.backend.dto.PurchaseResponse;
import com.mams.backend.model.*;
import com.mams.backend.model.enums.Role;
import com.mams.backend.repository.AssetRepository;
import com.mams.backend.repository.BaseRepository;
import com.mams.backend.repository.EquipmentTypeRepository;
import com.mams.backend.repository.PurchaseRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final AssetRepository assetRepository;
    private final AuditLogService auditLogService;

    public PurchaseService(PurchaseRepository purchaseRepository, BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, AssetRepository assetRepository, AuditLogService auditLogService) {
        this.purchaseRepository = purchaseRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.assetRepository = assetRepository;
        this.auditLogService = auditLogService;
    }


    @Transactional
    public PurchaseResponse create(PurchaseRequest request, User currentUser) {
        com.mams.backend.model.Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Base not found: " + request.getBaseId()));

        // a base commander / logistics officer can only record purchases for their own base
        if (currentUser.getRole() != Role.ADMIN
                && (currentUser.getBase() == null || !currentUser.getBase().getId().equals(base.getId()))) {
            throw new IllegalArgumentException("You can only record purchases for your own base");
        }

        EquipmentType equipmentType = equipmentTypeRepository.findById(request.getEquipmentTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment type not found: " + request.getEquipmentTypeId()));

        Purchase purchase = new Purchase();
        purchase.setBase(base);
        purchase.setEquipmentType(equipmentType);
        purchase.setQuantity(request.getQuantity());
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setCreatedBy(currentUser);
        purchase = purchaseRepository.save(purchase);

        // purchase adds straight to the base's on-hand stock
        Asset asset = assetRepository.findByBaseIdAndEquipmentTypeId(base.getId(), equipmentType.getId())
                .orElseGet(() -> new Asset(null, base, equipmentType, 0));
        asset.setQuantity(asset.getQuantity() + request.getQuantity());
        assetRepository.save(asset);

        auditLogService.log("CREATE_PURCHASE", "Purchase", purchase.getId(),
                "base=" + base.getName() + ", equipment=" + equipmentType.getName() + ", qty=" + request.getQuantity());

        return toResponse(purchase);
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> list(Long baseId, Long equipmentTypeId, LocalDate fromDate, LocalDate toDate, User currentUser) {
        // non-admins get forced to their own base regardless of what they pass in
        Long effectiveBaseId = baseId;
        if (currentUser.getRole() != Role.ADMIN) {
            effectiveBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
        }

        Specification<Purchase> spec = buildSpec(effectiveBaseId, equipmentTypeId, fromDate, toDate);

        return purchaseRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    private Specification<Purchase> buildSpec(Long baseId, Long equipmentTypeId, LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (baseId != null) {
                predicates.add(cb.equal(root.get("base").get("id"), baseId));
            }
            if (equipmentTypeId != null) {
                predicates.add(cb.equal(root.get("equipmentType").get("id"), equipmentTypeId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchaseDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("purchaseDate"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private PurchaseResponse toResponse(Purchase p) {
        return new PurchaseResponse(
                p.getId(),
                p.getBase().getId(),
                p.getBase().getName(),
                p.getEquipmentType().getId(),
                p.getEquipmentType().getName(),
                p.getQuantity(),
                p.getPurchaseDate(),
                p.getCreatedBy() != null ? p.getCreatedBy().getUsername() : null,
                p.getCreatedAt()
        );
    }
}
