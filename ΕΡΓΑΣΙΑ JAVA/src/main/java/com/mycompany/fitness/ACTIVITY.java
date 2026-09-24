/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class ACTIVITY {

    protected String sport;
    protected List<LAP> laps;
    private int age;
    private char gender;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public ACTIVITY(int age, char gender) {
        this.age = age;
        this.gender = gender;
    }
    
    public ACTIVITY(String sport) {
        this.sport = sport;
        this.laps = new ArrayList<>();
    }

    public void addLap(LAP lap) {
        laps.add(lap);
    }

    public String getSport() {
        return sport;
    }

    public List<LAP> getLaps() {
        return laps;
    }

    public double getTotalTimeSeconds() {
        double total = 0;
        for (LAP lap : laps) {
            total += lap.getTimeSeconds();
        }
        return total;
    }

    public double getTotalDistanceMeters() {
        double total = 0;
        for (LAP lap : laps) {
            total += lap.getDistanceMeters();
        }
        return total;
    }

    public double getTotalDistanceKm() {
        return getTotalDistanceMeters() / 1000.0;
    }

    public double getAvgSpeedKmh() {
        double hours = getTotalTimeSeconds() / 3600.0;
        if (hours == 0) return 0;
        return getTotalDistanceKm() / hours;
    }

    public int getAvgHeartRate() {
        int sum = 0;
        int count = 0;
        for (LAP lap : laps) {
            if (lap.getAvgHeartRate() > 0) {
                sum += lap.getAvgHeartRate();
                count++;
            }
        }
        if (count == 0) return 0;
        return sum / count;
    }
    
        public double calculateCalories(double weight) {
        double timeMinutes = getTotalTimeSeconds() / 60.0;
        double avgHR = getAvgHeartRate();

        if (gender == 'M') {
            return (-55.0969 + (0.6309 * avgHR) + (0.1988 * weight) + (0.2017 * age)) * timeMinutes / 4.184;
        } else if (gender == 'F') {
            return (-20.4022 + (0.4472 * avgHR) + (0.1263 * weight) + (0.074 * age)) * timeMinutes / 4.184;
        }
        return 0;
    }
        private LocalDate date;

        public LocalDate getDate() {
         return date;
        }
        
        public void setDate(LocalDate date) {
            this.date = date;
        }
    public abstract void printStats();
}
