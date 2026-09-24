/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.fitness;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */
public class FITNESS {

    public static void main(String[] args) {
        List<ACTIVITY> activities = new ArrayList<>();

         double weight=0;
         
        USERDATA userData = new USERDATA();
        
        if (args.length == 0) {
            System.out.println("Usage: java -jar FITNESS.jar [-w weight] file1.tcx file2.tcx ...");
            return;
        }
        int startIndex = 0;

        // Έλεγχος για -w
        if (args[0].equals("-w")) {
            if (args.length < 3) {
                System.out.println("Usage: java -jar FITNESS.jar -w weight file1.tcx file2.tcx ...");
                return;
            }
            try {
                weight = Double.parseDouble(args[1]);
                userData.setWeight(weight);
                startIndex = 2;
            } catch (Exception e) {
                System.out.println("Invalid weight value.");
                return;
            }
        }
        
        STATISTICS stats = new STATISTICS();
        DAILYCALORIESTRACKER tracker = new DAILYCALORIESTRACKER();

        // Ένα και μοναδικό loop για τα .tcx αρχεία
        for (int i = startIndex; i < args.length; i++) {

            String filename = args[i];

            ACTIVITY activity = TCXREADER.read(filename);

            if (activity == null) {
                System.out.println("Could not read activity from " + filename);
                continue;
            }

            activity.printStats();

            // Προσθήκη στα συνολικά στατιστικά
            stats.addActivity(activity);
            activities.add(activity);

            // Υπολογισμός θερμίδων αν δίνεται βάρος
           if (weight > 0) {
           double hours = activity.getTotalTimeSeconds() / 3600.0;
           double mu = getMuForSport(activity.getSport());
           double calories = mu * weight * hours;
           System.out.printf("Calories: %.2f kcal%n%n", calories);
            }

        }
       stats.printOverall();
       
       java.util.Scanner sc = new java.util.Scanner(System.in);
boolean running = true;

while (running) {
    System.out.println();
    System.out.println("===== MENU =====");
    System.out.println("1) Προσθήκη νέας δραστηριότητας");
    System.out.println("2) Εισαγωγή στοιχείων και τρόπος υπολογισμού θερμίδων ");
    System.out.println("3) Εισαγωγή στόχου και αποτελέσματα");
    System.out.println("4) Έξοδος");
    System.out.print("Επιλογή: ");

    String choice = sc.nextLine().trim();

    switch (choice) {
        case "1" -> {
             ACTIVITY act = ACTIVITYCREATOR.createActivityFromConsole();

             act.printStats();
             stats.addActivity(act);
             activities.add(act);


              if (userData.getWeight() > 0) {
                double calories = computeCaloriesForActivity(act, userData);
                System.out.printf("Calories: %.2f kcal%n%n", calories);
              }
             stats.printOverall();
            }
        case "2" -> {
            // Εισαγωγή στοιχείων μέσω της κλάσης USERDATA
             userData.readFromConsole(sc, userData.getWeight());

             // Αν θέλεις να συγχρονίσεις και τη μεταβλητή weight της main:
             weight = userData.getWeight();
        }

        case "3" -> {
             if (userData.getWeight() <= 0 || userData.getAge() <= 0 ||
             (userData.getGender() != 'M' && userData.getGender() != 'F') ||
             userData.getCaloriesMode() == 0) {

             System.out.println("Πρέπει πρώτα να εισάγεις στοιχεία (βάρος, ηλικία, φύλο, μέθοδο θερμίδων) από το 2.");
             break;
                }

             if (tracker.getDailyGoal() <= 0) {
                 while (true) {
                    System.out.print("Δώσε ημερήσιο στόχο θερμίδων (kcal, > 0): ");
                    String line = sc.nextLine().trim();
                     try {
                        double goal = Double.parseDouble(line);
                            if (goal > 0) {
                            tracker.setDailyGoal(goal);
                 break;
             } else {
                    System.out.println("Ο στόχος πρέπει να είναι > 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Μη έγκυρος αριθμός. Ξαναπροσπάθησε.");
            }
        }

         // Γεμίζουμε τον tracker με όλες τις δραστηριότητες
         for (ACTIVITY a : activities) {
        tracker.addActivity(a, userData);
         }

         // Εκτύπωση ημερήσιας αναφοράς
         tracker.printDailyReport();
}

        }

        case "4" -> {
            System.out.println("Έξοδος από το πρόγραμμα.");
            running = false;
        }

        default -> {
            System.out.println("Μη έγκυρη επιλογή. Δώσε 1, 2, 3 ή 4.");
        }
    }
}

sc.close();

       
 

    stats.printOverall();
    }
    private static double computeCaloriesForActivity(ACTIVITY activity, USERDATA userData) {

    double weight = userData.getWeight();
    if (weight <= 0) {
        return 0.0;
    }

    int mode = userData.getCaloriesMode();

    // Αν δεν έχει επιλέξει mode, default: απλή μέθοδος
    if (mode == 0 || mode == 1) {
        double hours = activity.getTotalTimeSeconds() / 3600.0;
        double mu = getMuForSport(activity.getSport());
        return mu * weight * hours;
    }

    // mode == 2 → με καρδιακούς παλμούς
    activity.setAge(userData.getAge());
    activity.setGender(userData.getGender());
    return activity.calculateCalories(weight);
}

    // συντελεστής μ ανά άθλημα
    private static double getMuForSport(String sport) {
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