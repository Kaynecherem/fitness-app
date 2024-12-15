package com.kalu.fitnessapp.repository;

import com.kalu.fitnessapp.entity.Tip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipRepository extends JpaRepository<Tip, Long> {
}
