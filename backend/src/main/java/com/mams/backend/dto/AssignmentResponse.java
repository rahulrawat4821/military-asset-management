package com.mams.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentResponse {
    private Long id;
    private Long baseId;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentTypeName;
    private String personnelName;
    private Integer quantity;
    private LocalDate assignmentDate;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;

    public AssignmentResponse(Long id, Long baseId, String baseName, Long equipmentTypeId, String equipmentTypeName,
                               String personnelName, Integer quantity, LocalDate assignmentDate, String status,
                               String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.baseId = baseId;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentTypeName = equipmentTypeName;
        this.personnelName = personnelName;
        this.quantity = quantity;
        this.assignmentDate = assignmentDate;
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

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
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
