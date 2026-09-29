package com.mams.backend.model;

import com.mams.backend.model.enums.TransferStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_base_id", nullable = false)
    private Base fromBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_base_id", nullable = false)
    private Base toBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDate transferDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status = TransferStatus.COMPLETED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Transfer() {
    }

    public Transfer(Long id, Base fromBase, Base toBase, EquipmentType equipmentType, Integer quantity,
                     LocalDate transferDate, TransferStatus status, User createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.fromBase = fromBase;
        this.toBase = toBase;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.transferDate = transferDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Base getFromBase() {
        return fromBase;
    }

    public void setFromBase(Base fromBase) {
        this.fromBase = fromBase;
    }

    public Base getToBase() {
        return toBase;
    }

    public void setToBase(Base toBase) {
        this.toBase = toBase;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
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

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
