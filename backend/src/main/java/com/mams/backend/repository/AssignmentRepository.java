package com.mams.backend.repository;

import com.mams.backend.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {

    // Total quantity assigned in the window, regardless of current status (assigned or later expended)
    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE " +
            "(:baseId IS NULL OR a.base.id = :baseId) AND " +
            "(:equipmentTypeId IS NULL OR a.equipmentType.id = :equipmentTypeId) AND " +
            "(:fromDate IS NULL OR a.assignmentDate >= :fromDate) AND " +
            "(:toDate IS NULL OR a.assignmentDate <= :toDate)")
    Integer sumAssignedQuantity(@Param("baseId") Long baseId,
                                @Param("equipmentTypeId") Long equipmentTypeId,
                                @Param("fromDate") LocalDate fromDate,
                                @Param("toDate") LocalDate toDate);

    // Quantity currently marked EXPENDED, among assignments made in the window
    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE a.status = com.mams.backend.model.enums.AssignmentStatus.EXPENDED AND " +
            "(:baseId IS NULL OR a.base.id = :baseId) AND " +
            "(:equipmentTypeId IS NULL OR a.equipmentType.id = :equipmentTypeId) AND " +
            "(:fromDate IS NULL OR a.assignmentDate >= :fromDate) AND " +
            "(:toDate IS NULL OR a.assignmentDate <= :toDate)")
    Integer sumExpendedQuantity(@Param("baseId") Long baseId,
                                @Param("equipmentTypeId") Long equipmentTypeId,
                                @Param("fromDate") LocalDate fromDate,
                                @Param("toDate") LocalDate toDate);
}
