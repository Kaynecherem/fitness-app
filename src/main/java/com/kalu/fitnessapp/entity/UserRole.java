package com.kalu.fitnessapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN("This is the admin user role who has access to view and modify all data on the application."),
    STUDENT("This is the student user role who has access to view and modify their own data on the application.");


    private final String description;
}
