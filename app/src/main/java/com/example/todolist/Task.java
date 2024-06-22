package com.example.todolist;

public class Task {
    private String name;
    private String description;
    private String date;
    private String time;

    public Task(String name, String description, String date, String time) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    // Getters and setters (omitted for brevity)

    @Override
    public String toString() {
        return "\n-> Task Name: "+name + "\n-> Task Description: " + description + "\n-> Date: " + date + "\n-> Time: " + time;
    }
}


