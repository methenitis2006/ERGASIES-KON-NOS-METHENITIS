/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */
import java.util.Scanner;

public class USERDATA {

    private double weight;      // κιλά
    private int age;            // ηλικία
    private char gender;        // 'M' ή 'F'
    private int caloriesMode;   // 0 = κανένας, 1 = μ*w*t, 2 = HR τύπος

    public USERDATA() {
        this.weight = -1;
        this.age = -1;
        this.gender = 'U';   // Unknown
        this.caloriesMode = 0;
    }

    public USERDATA(double weight, int age, char gender, int caloriesMode) {
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        this.caloriesMode = caloriesMode;
    }

    // ===== GETTERS / SETTERS =====

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

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

    public int getCaloriesMode() {
        return caloriesMode;
    }

    public void setCaloriesMode(int caloriesMode) {
        this.caloriesMode = caloriesMode;
    }

    // ===== ΕΙΣΑΓΩΓΗ ΣΤΟΙΧΕΙΩΝ ΑΠΟ ΚΟΝΣΟΛΑ =====
    // currentWeight: αν έχεις ήδη βάρος από -w, το περνάς εδώ για να μην το ξαναρωτήσει
    public void readFromConsole(Scanner sc, double currentWeight) {

        // Βάρος (αν δεν υπάρχει ήδη)
        if (currentWeight <= 0) {
            while (true) {
                System.out.print("Δώσε βάρος σε kg: ");
                String line = sc.nextLine().trim();
                try {
                    double w = Double.parseDouble(line);
                    if (w > 0) {
                        this.weight = w;
                        break;
                    } else {
                        System.out.println("Το βάρος πρέπει να είναι μεγαλύτερο από 0.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Μη έγκυρος αριθμός. Ξαναπροσπάθησε.");
                }
            }
        } else {
            // αν έχεις ήδη βάρος από -w, το κρατάμε
            this.weight = currentWeight;
        }

        // Φύλο
        while (true) {
            System.out.print("Δώσε φύλο: ");
            String g = sc.nextLine().trim().toUpperCase();
            if (g.equals("MALE") || g.equals("FEMALE") || g.equals("Male") || g.equals("Female")) {
                this.gender = g.charAt(0);
                break;
            } else {
                System.out.println("Μη έγκυρο φύλο. Δώσε Άνδρας ή Γυναίκα.");
            }
        }

        // Ηλικία
        while (true) {
            System.out.print("Δώσε ηλικία: ");
            String line = sc.nextLine().trim();
            try {
                int a = Integer.parseInt(line);
                if (a > 0) {
                    this.age = a;
                    break;
                } else {
                    System.out.println("Η ηλικία πρέπει να είναι μεγαλύτερη από 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Μη έγκυρη ηλικία. Ξαναπροσπάθησε.");
            }
        }

        // Επιλογή τρόπου υπολογισμού θερμίδων
        while (true) {
            System.out.println("Επίλεξε τρόπο υπολογισμού θερμίδων:");
            System.out.println("1) Μέυοδος με βάρος και άσκηση");
            System.out.println("2) Μέθοδος με καρδιακούς παλμούς");
            System.out.print("Επιλογή (1 ή 2): ");
            String line = sc.nextLine().trim();
            if (line.equals("1") || line.equals("2")) {
                this.caloriesMode = Integer.parseInt(line);
                break;
            } else {
                System.out.println("Δώσε 1 ή 2.");
            }
        }

        System.out.println("Τα στοιχεία σου αποθηκεύτηκαν.");
    }
}

