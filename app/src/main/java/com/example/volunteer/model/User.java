package com.example.volunteer.model;

public class User {
    private String firstName;
    private String lastName;
    private int volunteerHours;

    public User(String firstName, String lastName, int volunteerHours) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.volunteerHours = volunteerHours;
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
