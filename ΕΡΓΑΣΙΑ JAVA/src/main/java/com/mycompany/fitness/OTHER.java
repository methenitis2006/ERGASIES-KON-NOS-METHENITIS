/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */
public class OTHER extends ACTIVITY {

    public OTHER() {
        super("Other");
    }

    @Override
    public void printStats() {
        System.out.println("Activity: Other");

        int totalSeconds = (int) getTotalTimeSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        System.out.printf("Total Time: %d:%02d%n", minutes, seconds);
        System.out.printf("Total Distance: %.2f km%n", getTotalDistanceKm());

        int avgHR = getAvgHeartRate();
        if (avgHR > 0) {
            System.out.printf("Avg Heart Rate: %d bpm%n", avgHR);
        }

        System.out.println();
    }
}

