package com.mams.backend.controller;

import com.mams.backend.model.Base;
import com.mams.backend.model.EquipmentType;
import com.mams.backend.repository.BaseRepository;
import com.mams.backend.repository.EquipmentTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// small read-only endpoints so the frontend can populate base / equipment
// dropdowns on the purchase, transfer and assignment forms
@RestController
public class LookupController {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    public LookupController(BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository) {
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }


    @GetMapping("/api/bases")
    public ResponseEntity<List<Base>> listBases() {
        return ResponseEntity.ok(baseRepository.findAll());
    }

    @GetMapping("/api/equipment-types")
    public ResponseEntity<List<EquipmentType>> listEquipmentTypes() {
        return ResponseEntity.ok(equipmentTypeRepository.findAll());
    }
}
