package com.mams.backend;

import com.mams.backend.model.*;
import com.mams.backend.model.enums.EquipmentCategory;
import com.mams.backend.model.enums.Role;
import com.mams.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Only runs when the DB is empty, so it's safe to restart the app without
// duplicating data every time.
@Component
public class DataSeeder implements CommandLineRunner {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(BaseRepository baseRepository, EquipmentTypeRepository equipmentTypeRepository, UserRepository userRepository, AssetRepository assetRepository, PasswordEncoder passwordEncoder) {
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        Base baseAlpha = baseRepository.save(new Base(null, "Base Alpha", "Jaisalmer, Rajasthan"));
        Base baseBravo = baseRepository.save(new Base(null, "Base Bravo", "Leh, Ladakh"));

        EquipmentType rifle = equipmentTypeRepository.save(new EquipmentType(null, "5.56mm Rifle", EquipmentCategory.WEAPON));
        EquipmentType truck = equipmentTypeRepository.save(new EquipmentType(null, "Light Utility Vehicle", EquipmentCategory.VEHICLE));
        EquipmentType ammo = equipmentTypeRepository.save(new EquipmentType(null, "5.56mm Ammunition (rounds)", EquipmentCategory.AMMUNITION));

        User admin = new User();
        admin.setUsername("admin");
        admin.setFullName("System Admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User commander = new User();
        commander.setUsername("commander1");
        commander.setFullName("Col. Arvind Rathore");
        commander.setPassword(passwordEncoder.encode("commander123"));
        commander.setRole(Role.BASE_COMMANDER);
        commander.setBase(baseAlpha);
        userRepository.save(commander);

        User logistics = new User();
        logistics.setUsername("logistics1");
        logistics.setFullName("Maj. Sneha Iyer");
        logistics.setPassword(passwordEncoder.encode("logistics123"));
        logistics.setRole(Role.LOGISTICS_OFFICER);
        logistics.setBase(baseAlpha);
        userRepository.save(logistics);

        // starting stock so the dashboard isn't empty on first login
        assetRepository.save(new Asset(null, baseAlpha, rifle, 120));
        assetRepository.save(new Asset(null, baseAlpha, truck, 8));
        assetRepository.save(new Asset(null, baseAlpha, ammo, 5000));
        assetRepository.save(new Asset(null, baseBravo, rifle, 60));
        assetRepository.save(new Asset(null, baseBravo, truck, 4));

        System.out.println("Seed data loaded: admin/admin123, commander1/commander123, logistics1/logistics123");
    }
}
