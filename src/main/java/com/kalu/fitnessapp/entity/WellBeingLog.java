package com.kalu.fitnessapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wellbeing_logs")
public class WellBeingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private String mentalHealthStatus;
    private String physicalHealthStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
