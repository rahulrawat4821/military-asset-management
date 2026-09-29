package com.mams.backend.repository;

import com.mams.backend.model.Base;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository extends JpaRepository<Base, Long> {
}
