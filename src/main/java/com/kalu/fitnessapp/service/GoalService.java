package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.UserDeletedEvent;
import com.kalu.fitnessapp.entity.Goal;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.repository.GoalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

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

    //This deletes User Goals whenever the UserDeleteEvent occurs
    @TransactionalEventListener
    public void userDeletionListener(UserDeletedEvent userDeletedEvent) {
        goalRepository.deleteByUser(userDeletedEvent.user());
    }
}
