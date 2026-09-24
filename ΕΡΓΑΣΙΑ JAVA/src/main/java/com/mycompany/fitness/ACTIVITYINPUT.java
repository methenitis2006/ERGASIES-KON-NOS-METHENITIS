/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */

public class ACTIVITYINPUT {

    private String type;
    private double timeSeconds;
    private double distanceMeters;
    private int avgHeartRate;

    public ACTIVITYINPUT() {}

    public ACTIVITYINPUT(String type, double timeSeconds, double distanceMeters, int avgHeartRate) {
        this.type = type;
        this.timeSeconds = timeSeconds;
        this.distanceMeters = distanceMeters;
        this.avgHeartRate = avgHeartRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(double timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }
}

