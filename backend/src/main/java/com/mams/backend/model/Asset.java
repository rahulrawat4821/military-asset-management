package com.mams.backend.model;

import jakarta.persistence.*;

// Represents current on-hand quantity of one equipment type at one base.
// Opening/closing balance and net movement are derived from this + the
// purchase/transfer/assignment tables, not stored directly here.
@Entity
@Table(name = "assets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"base_id", "equipment_type_id"})
})
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer quantity = 0;

    public Asset() {
    }

    public Asset(Long id, Base base, EquipmentType equipmentType, Integer quantity) {
        this.id = id;
        this.base = base;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
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
}
