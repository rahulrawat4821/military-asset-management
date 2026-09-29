package com.mams.backend.repository;

import com.mams.backend.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

    // Transfers OUT of a base (or all bases, if baseId is null) in the given window
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
            "(:baseId IS NULL OR t.fromBase.id = :baseId) AND " +
            "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
            "(:fromDate IS NULL OR t.transferDate >= :fromDate) AND " +
            "(:toDate IS NULL OR t.transferDate <= :toDate)")
    Integer sumOutgoing(@Param("baseId") Long baseId,
                        @Param("equipmentTypeId") Long equipmentTypeId,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate);

    // Transfers IN to a base (or all bases, if baseId is null) in the given window
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
            "(:baseId IS NULL OR t.toBase.id = :baseId) AND " +
            "(:equipmentTypeId IS NULL OR t.equipmentType.id = :equipmentTypeId) AND " +
            "(:fromDate IS NULL OR t.transferDate >= :fromDate) AND " +
            "(:toDate IS NULL OR t.transferDate <= :toDate)")
    Integer sumIncoming(@Param("baseId") Long baseId,
                        @Param("equipmentTypeId") Long equipmentTypeId,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate);
}
