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
import java.util.HashMap;
import java.util.Map;

public class DAILYCALORIESTRACKER {

    private double dailyGoal;
    private final Map<LocalDate, Double> caloriesPerDay = new HashMap<>();

    public double getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(double dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public Map<LocalDate, Double> getCaloriesPerDay() {
        return caloriesPerDay;
    }

    /**
     * Προσθέτει τις θερμίδες μιας δραστηριότητας στην αντίστοιχη μέρα.
     * Χρησιμοποιεί τα στοιχεία του χρήστη (βάρος, ηλικία, φύλο, μέθοδο).
     */
    public void addActivity(ACTIVITY activity, USERDATA userData) {

        LocalDate date = activity.getDate();  // ΠΡΕΠΕΙ να υπάρχει getDate() στο ACTIVITY

        double calories = 0.0;

        // Ανάλογα με τη μέθοδο που έχει επιλέξει ο χρήστης:
        if (userData.getCaloriesMode() == 1) {
            // Απλή μέθοδος: C = μ * w * t(σε ώρες)
            double hours = activity.getTotalTimeSeconds() / 3600.0;
            double mu = getMuForSport(activity.getSport());
            calories = mu * userData.getWeight() * hours;

        } else if (userData.getCaloriesMode() == 2) {
            // Μέθοδος με HR τύπους
            activity.setAge(userData.getAge());
            activity.setGender(userData.getGender());
            calories = activity.calculateCalories(userData.getWeight());
        }

        // Σωρευτικά ανά ημέρα
        caloriesPerDay.put(
                date,
                caloriesPerDay.getOrDefault(date, 0.0) + calories
        );
    }

    /**
     * Εκτυπώνει για κάθε μέρα:
     * - πόσες θερμίδες κάηκαν,
     * - αν επιτεύχθηκε ο στόχος,
     * - πόσες απομένουν (ή πόσες τον ξεπέρασαν).
     */
    public void printDailyReport() {

        for (Map.Entry<LocalDate, Double> entry : caloriesPerDay.entrySet()) {

            LocalDate date = entry.getKey();
            double burned = entry.getValue();

            System.out.println("Date: " + date);
            System.out.printf("Burned: %.2f kcal%n", burned);
            System.out.printf("Goal:   %.2f kcal%n", dailyGoal);

            if (burned >= dailyGoal) {
                System.out.println("Goal achieved (έχεις φτάσει ή ξεπεράσει τον στόχο)");
                if (burned > dailyGoal) {
                    System.out.printf("Υπέρβαση στόχου: %.2f kcal%n", burned - dailyGoal);
                }
            } else {
                double remaining = dailyGoal - burned;
                System.out.printf("Δεν επιτεύχθηκε ακόμη. Απομένουν: %.2f kcal%n", remaining);
            }

            System.out.println();
        }
    }

    // Συντελεστής μ ανάλογα με το sport
    private double getMuForSport(String sport) {
        if (sport == null) return 5.0;
        sport = sport.toLowerCase();
        return switch (sport) {
            case "running" -> 9.0;
            case "walking" -> 3.5;
            case "cycling", "biking" -> 6.0;
            case "swimming" -> 8.0;
            default -> 5.0;
        };
    }
}
