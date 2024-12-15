package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.entity.User;
import com.kalu.fitnessapp.entity.WellBeingLog;
import com.kalu.fitnessapp.repository.WellBeingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WellBeingLogService {

    @Autowired
    private WellBeingLogRepository logRepository;

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
        return "Wellbeing Log is removed: "+id;
    }
}
