/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */
public class WALKING extends ACTIVITY {

    public WALKING() {
        super("Walking");
    }

    @Override
    public void printStats() {
        System.out.println("Activity: Walking");

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