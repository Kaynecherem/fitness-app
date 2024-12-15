package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.entity.Goal;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    public List<Goal> getGoalsByUser(User user) {
        return goalRepository.findByUser(user);
    }

    // New method to get a goal by ID
    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    // New method to update a goal
    public Goal updateGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    // New method to delete a goal
    public String deleteGoal(Long id) {
        goalRepository.deleteById(id);
        return "Goal is removed: "+id;
    }
}
