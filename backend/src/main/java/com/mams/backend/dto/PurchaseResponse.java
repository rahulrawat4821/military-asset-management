package com.mams.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseResponse {
    private Long id;
    private Long baseId;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private Integer quantity;
    private LocalDate purchaseDate;
    private String createdBy;
    private LocalDateTime createdAt;

    public PurchaseResponse(Long id, Long baseId, String baseName, Long equipmentTypeId, String equipmentTypeName,
                             Integer quantity, LocalDate purchaseDate, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.baseId = baseId;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public String getEquipmentTypeName() {
        return equipmentTypeName;
    }

    public void setEquipmentTypeName(String equipmentTypeName) {
        this.equipmentTypeName = equipmentTypeName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
