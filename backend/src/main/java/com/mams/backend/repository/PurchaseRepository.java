package com.mams.backend.repository;

import com.mams.backend.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>, JpaSpecificationExecutor<Purchase> {

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Purchase p WHERE " +
            "(:baseId IS NULL OR p.base.id = :baseId) AND " +
            "(:equipmentTypeId IS NULL OR p.equipmentType.id = :equipmentTypeId) AND " +
            "(:fromDate IS NULL OR p.purchaseDate >= :fromDate) AND " +
            "(:toDate IS NULL OR p.purchaseDate <= :toDate)")
    Integer sumQuantity(@Param("baseId") Long baseId,
                         @Param("equipmentTypeId") Long equipmentTypeId,
                         @Param("fromDate") LocalDate fromDate,
                         @Param("toDate") LocalDate toDate);
}
