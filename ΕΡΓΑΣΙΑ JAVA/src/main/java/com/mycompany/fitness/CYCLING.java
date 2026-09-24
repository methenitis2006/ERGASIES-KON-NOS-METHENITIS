/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */

public class CYCLING extends ACTIVITY {

    public CYCLING() {
        super("Cycling");
    }

    @Override
    public void printStats() {
        System.out.println("Activity: Cycling");

        // Total time: seconds -> mm:ss
        int totalSeconds = (int) getTotalTimeSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        System.out.printf("Total Time: %d:%02d%n", minutes, seconds);

        double distanceKm = getTotalDistanceKm();
        System.out.printf("Total Distance: %.2f km%n", distanceKm);

        double hours = getTotalTimeSeconds() / 3600.0;
        double avgSpeed = (hours > 0) ? distanceKm / hours : 0.0;
        System.out.printf("Avg Speed: %.2f km/h%n", avgSpeed);

        int avgHR = getAvgHeartRate();
        if (avgHR > 0) {
            System.out.printf("Avg Heart Rate: %d bpm%n", avgHR);
        }

        System.out.println();
    }
}
