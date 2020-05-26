package com.example.volunteer.model;

import java.io.Serializable;

public class User implements Serializable {
    private String email;
    private String firstName;
    private String lastName;
    private int volunteerHours;

    public User(String email, String firstName, String lastName, int volunteerHours) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.volunteerHours = volunteerHours;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getVolunteerHours() {
        return volunteerHours;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setVolunteerHours(int volunteerHours) {
        this.volunteerHours = volunteerHours;
    }
}