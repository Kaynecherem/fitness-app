package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.UserDeletedEvent;
import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import com.kalu.fitnessapp.repository.WellBeingLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WellBeingLogService {

    private final WellBeingLogRepository logRepository;

    public WellBeingLog createLog(WellBeingLog log) {
        return logRepository.save(log);
    }

    public List<WellBeingLog> getLogsByUser(User user) {
        return logRepository.findByUser(user);
    }

    public Optional<WellBeingLog> getLogById(Long id) {
        return logRepository.findById(id);
    }

    public WellBeingLog updateLog(WellBeingLog log) {
        return logRepository.save(log);
    }

    public String deleteLog(Long id) {
        logRepository.deleteById(id);
        return "Wellbeing Log is removed: " + id;
    }

    @TransactionalEventListener
    public void userDeletionListener(UserDeletedEvent userDeletedEvent) {
        logRepository.deleteByUser(userDeletedEvent.user());
    }
}
