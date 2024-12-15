package com.kalu.fitnessapp.service;

import com.kalu.fitnessapp.entity.Tip;
import com.kalu.fitnessapp.repository.TipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipService {

    @Autowired
    private TipRepository tipRepository;

    public Tip createTip(Tip tip) {
        return tipRepository.save(tip);
    }

    public List<Tip> getAllTips() {
        return tipRepository.findAll();
    }

    public Optional<Tip> getTipById(Long id) {
        return tipRepository.findById(id);
    }

    public Tip updateTip(Tip tip) {
        return tipRepository.save(tip);
    }

    public String deleteTip(Long id) {
        tipRepository.deleteById(id);
        return "Tip is removed: "+id;
    }
}
