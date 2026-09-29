package com.mams.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TransferRequest {

    @NotNull(message = "fromBaseId is required")
    private Long fromBaseId;

    @NotNull(message = "toBaseId is required")
    private Long toBaseId;

    @NotNull(message = "equipmentTypeId is required")
    private Long equipmentTypeId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "transferDate is required")
    private LocalDate transferDate;

    public Long getFromBaseId() {
        return fromBaseId;
    }

    public void setFromBaseId(Long fromBaseId) {
        this.fromBaseId = fromBaseId;
    }

    public Long getToBaseId() {
        return toBaseId;
    }

    public void setToBaseId(Long toBaseId) {
        this.toBaseId = toBaseId;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
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
}
