package com.kalu.fitnessapp.repository;

import com.kalu.fitnessapp.entity.Goal;
import com.kalu.fitnessapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
}
