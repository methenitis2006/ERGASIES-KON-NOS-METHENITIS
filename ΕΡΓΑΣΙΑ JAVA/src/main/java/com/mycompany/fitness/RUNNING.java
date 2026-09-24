/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */
public class RUNNING extends ACTIVITY{
    public RUNNING() {
        super("Running");   
    }

    public double getAvgPaceMinPerKm() {
        double km = getTotalDistanceKm();
        double minutes = getTotalTimeSeconds() / 60.0;
        return km == 0 ? 0 : minutes / km;
    }

    @Override
public void printStats() {
    System.out.println("Activity: Running");

    int totalSeconds = (int) getTotalTimeSeconds();
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;

    System.out.printf("Total Time: %d:%02d%n", minutes, seconds);

    System.out.printf("Total Distance: %.2f km%n", getTotalDistanceKm());
    System.out.printf("Avg Speed: %.2f km/h%n", getAvgSpeedKmh());

    int avgHR = getAvgHeartRate();
    if (avgHR > 0) {
        System.out.printf("Avg Heart Rate: %d bpm%n", avgHR);
    }

    System.out.println();
}
}