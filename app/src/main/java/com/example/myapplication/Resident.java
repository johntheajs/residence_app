package com.example.myapplication;

public class Resident {
    private int id;
    private String name;
    private int age;

    public Resident(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
