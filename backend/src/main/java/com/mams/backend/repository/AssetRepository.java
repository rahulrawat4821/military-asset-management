package com.mams.backend.repository;

import com.mams.backend.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByBaseIdAndEquipmentTypeId(Long baseId, Long equipmentTypeId);
    java.util.List<Asset> findByBaseId(Long baseId);
}
