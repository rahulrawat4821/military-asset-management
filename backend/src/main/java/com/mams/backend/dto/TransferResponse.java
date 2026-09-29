package com.mams.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransferResponse {
    private Long id;
    private Long fromBaseId;
    private String fromBaseName;
    private Long toBaseId;
    private String toBaseName;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private Integer quantity;
    private LocalDate transferDate;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;

    public TransferResponse(Long id, Long fromBaseId, String fromBaseName, Long toBaseId, String toBaseName,
                             Long equipmentTypeId, String equipmentTypeName, Integer quantity,
                             LocalDate transferDate, String status, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.fromBaseId = fromBaseId;
        this.fromBaseName = fromBaseName;
        this.toBaseId = toBaseId;
        this.toBaseName = toBaseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromBaseId() {
        return fromBaseId;
    }

    public void setFromBaseId(Long fromBaseId) {
        this.fromBaseId = fromBaseId;
    }

    public String getFromBaseName() {
        return fromBaseName;
    }

    public void setFromBaseName(String fromBaseName) {
        this.fromBaseName = fromBaseName;
    }

    public Long getToBaseId() {
        return toBaseId;
    }

    public void setToBaseId(Long toBaseId) {
        this.toBaseId = toBaseId;
    }

    public String getToBaseName() {
        return toBaseName;
    }

    public void setToBaseName(String toBaseName) {
        this.toBaseName = toBaseName;
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

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
