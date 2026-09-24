/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */
public class STATISTICS {

    private double totalTimeSeconds = 0;
    private double totalDistanceKm = 0;
    private int activityCount = 0;

    public void addActivity(ACTIVITY a) {
        totalTimeSeconds += a.getTotalTimeSeconds();
        totalDistanceKm += a.getTotalDistanceKm();
        activityCount++;
    }

    public void printOverall() {
        System.out.println("===== Overall Stats =====");

        int totalSeconds = (int) totalTimeSeconds;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        System.out.printf("Total Time: %d:%02d%n", minutes, seconds);
        System.out.printf("Total Distance: %.2f km%n", totalDistanceKm);
    }
}

