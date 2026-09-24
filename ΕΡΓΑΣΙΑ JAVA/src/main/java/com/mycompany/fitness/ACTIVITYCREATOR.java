/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

import java.util.Scanner;

/**
 *
 * @author user
 */

public class ACTIVITYCREATOR {

    // Δημιουργεί ACTIVITY με ασφαλές input από τον χρήστη
    public static ACTIVITY createActivityFromConsole() {
        Scanner sc = new Scanner(System.in);

        ACTIVITYINPUT input = new ACTIVITYINPUT();

        // ----- Τύπος δραστηριότητας -----
        String type;
        while (true) {
            System.out.print("Τύπος (Running/Walking/Cycling/Swimming): ");
            type = sc.nextLine().trim();

            if (type.equalsIgnoreCase("running") ||
                type.equalsIgnoreCase("walking") ||
                type.equalsIgnoreCase("cycling") ||
                type.equalsIgnoreCase("swimming")) {
                input.setType(type);
                break;
            } else {
                System.out.println("Μη έγκυρος τύπος δραστηριότητας. Δοκίμασε: Running, Walking, Biking, Swimming.");
            }
        }

        // ----- Χρόνος -----
        double timeSeconds = readPositiveDouble(sc, "Χρόνος σε δευτερόλεπτα: ");
        input.setTimeSeconds(timeSeconds);

        // ----- Απόσταση -----
        double distanceMeters = readNonNegativeDouble(sc, "Απόσταση σε μέτρα: ");
        input.setDistanceMeters(distanceMeters);

        // ----- Μέσος παλμός -----
        int avgHeartRate = readNonNegativeInt(sc, "Μέσος παλμός: ");
        input.setAvgHeartRate(avgHeartRate);

        // ----- Δημιουργία ACTIVITY ανάλογα με τον τύπο -----
        ACTIVITY activity = null;
        switch (input.getType().toLowerCase()) {
            case "running" -> activity = new RUNNING();
            case "walking" -> activity = new WALKING();
            case "cycling"  -> activity = new CYCLING();
            case "swimming"-> activity = new SWIMMING();
            default -> {
                activity = new RUNNING();
            }
        }

        // Δημιουργία ενός LAP με τα δεδομένα
        LAP lap = new LAP(
                input.getTimeSeconds(),
                input.getDistanceMeters(),
                input.getAvgHeartRate()
        );

        activity.addLap(lap);

        return activity;
    }

   //Μέθοδος για έλεγχο εγκυρότητας

    private static double readPositiveDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("Η τιμή πρέπει να είναι μεγαλύτερη από 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Μη έγκυρος αριθμός. Δώσε ξανά.");
            }
        }
    }

    private static double readNonNegativeDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value >= 0) {
                    return value;
                } else {
                    System.out.println("Η τιμή δεν μπορεί να είναι αρνητική.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Μη έγκυρος αριθμός. Δώσε ξανά.");
            }
        }
    }

    private static int readNonNegativeInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= 0) {
                    return value;
                } else {
                    System.out.println("Η τιμή δεν μπορεί να είναι αρνητική.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Μη έγκυρος ακέραιος. Δώσε ξανά.");
            }
        }
    }
}
