package com.kalu.fitnessapp.repository;

import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WellBeingLogRepository  extends JpaRepository<WellBeingLog, Long> {
    List<WellBeingLog> findByUser(User user);
    void deleteByUser(User user);
}
