#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>

//=============================
// 1. ΔΟΜΕΣ ΔΕΔΟΜΕΝΩΝ & ENUMS
//=============================

typedef enum {
    GENERAL,
    CARDIOLOGY,
    PEDIATRICS,
    ORTHOPEDICS,
    NEUROLOGY,
    OTHER
} Specialty;

const char* SPECIALTY_NAMES[] = {
    "GENERAL",
    "CARDIOLOGY",
    "PEDIATRICS",
    "ORTHOPEDICS",
    "NEUROLOGY",
    "OTHER"
};

typedef struct {
    int patient_id;
    char first_name[31];
    char last_name[31];
    int age;
    char phone[20];
} Patient;

typedef struct {
    int doctor_id;
    char first_name[31];
    char last_name[31];
    Specialty specialty;
} Doctor;

typedef struct {
    int patient_id;
    int doctor_id;
    char date[11];
    char time[6];
} Appointment;

int patient_sort_criterion = 1;

// ============================================================
// 2. ΒΟΗΘΗΤΙΚΕΣ ΣΥΝΑΡΤΗΣΕΙΣ & ΕΛΕΓΧΟΙ ΟΡΘΟΤΗΤΑΣ (VALIDATION)
// ============================================================

void strip_newline(char *str) {
    size_t len = strlen(str);
    if (len > 0 && str[len - 1] == '\n') {
        str[len - 1] = '\0';
    }
}

bool is_numeric(const char *str) {
    if (strlen(str) == 0) return false;
    for (int i = 0; str[i] != '\0'; i++) {
        if (!isdigit((unsigned char)str[i])) return false;
    }
    return true;
}

//Εύρεση ημέρας (0=Κυριακή, 1=Δευτέρα, ..., 6=Σάββατο)
int get_weekday(int y, int m, int d) {
    static int t[] = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
    if (m < 3) y -= 1;
    return (y + y/4 - y/100 + y/400 + t[m-1] + d) % 7;
}

bool is_working_day(const char *date_str) {
    int year, month, day;
    if (sscanf(date_str, "%d-%d-%d", &year, &month, &day) != 3) return false;
    int wday = get_weekday(year, month, day);
    if (wday == 0 || wday == 6) return false; 
    return true;
}

bool is_valid_time_slot(const char *time_str) {
    int hours, minutes;
    if (sscanf(time_str, "%d:%d", &hours, &minutes) != 2) return false;
    if (hours < 8 || hours > 14) return false;
    if (hours == 14 && minutes > 30) return false; 
    if (minutes != 0 && minutes != 30) return false; 
    return true;
}

int find_patient_idx(Patient *patients, int count, int id) {
    for (int i = 0; i < count; i++) if (patients[i].patient_id == id) return i;
    return -1;
}

int find_doctor_idx(Doctor *doctors, int count, int id) {
    for (int i = 0; i < count; i++) if (doctors[i].doctor_id == id) return i;
    return -1;
}

bool is_slot_busy(Appointment *appointments, int count, int doc_id, int pat_id, const char *date, const char *time) {
    for (int i = 0; i < count; i++) {
        if (strcmp(appointments[i].date, date) == 0 && strcmp(appointments[i].time, time) == 0) {
            if (appointments[i].doctor_id == doc_id) {
                printf("Σφάλμα: Ο ιατρός είναι ήδη απασχολημένος εκείνη την ώρα.\n");
                return true;
            }
            if (appointments[i].patient_id == pat_id) {
                printf("Σφάλμα: Ο ασθενής έχει ήδη άλλο ραντεβού την ίδια ώρα.\n");
                return true;
            }
        }
    }
    return false;
}

// ===============================================
// 3. ΕΝΟΤΗΤΑ: ΔΙΑΧΕΙΡΙΣΗ ΑΣΘΕΝΩΝ (Επιλογές 1-5)
// ===============================================

void add_patient(Patient **patients, int *count) {
    Patient p;
    char temp[100];
    printf("\n=== Προσθήκη Νέου Ασθενούς ===\n");

    while (1) {
        printf("ID Ασφάλισης: "); fgets(temp, sizeof(temp), stdin); strip_newline(temp);
        if (is_numeric(temp)) {
            p.patient_id = atoi(temp);
            if (find_patient_idx(*patients, *count, p.patient_id) != -1) {
                printf("Σφάλμα: Το ID υπάρχει ήδη.\n");
            } else break;
        } else printf("Σφάλμα: Δώστε μόνο ψηφία.\n");
    }
    printf("Όνομα (max 30): "); fgets(p.first_name, 31, stdin); strip_newline(p.first_name);
    printf("Επώνυμο (max 30): "); fgets(p.last_name, 31, stdin); strip_newline(p.last_name);
    while (1) {
        printf("Ηλικία: "); fgets(temp, sizeof(temp), stdin); strip_newline(temp);
        if (is_numeric(temp)) { p.age = atoi(temp); break; }
        else printf("Σφάλμα: Δώστε έγκυρη ηλικία.\n");
    }
    while (1) {
        printf("Αριθμός Τηλεφώνου: "); fgets(p.phone, 20, stdin); strip_newline(p.phone);
        if (is_numeric(p.phone)) break;
        else printf("Σφάλμα: Το τηλέφωνο πρέπει να έχει μόνο ψηφία.\n");
    }

    *patients = realloc(*patients, (*count + 1) * sizeof(Patient));
    (*patients)[*count] = p;
    (*count)++;
    printf("Ο ασθενής καταχωρήθηκε επιτυχώς!\n");
}

void edit_patient(Patient *patients, int count) {
    char temp[100];
    printf("\n=== Επεξεργασία Ασθενούς ===\n");
    printf("Δώστε ID Ασθενούς: "); fgets(temp, sizeof(temp), stdin);
    int idx = find_patient_idx(patients, count, atoi(temp));
    if (idx == -1) { printf("Ο ασθενής δεν βρέθηκε.\n"); return; }

    int choice;
    do {
        printf("\nΤροποποίηση για %s %s:\n", patients[idx].first_name, patients[idx].last_name);
        printf("1. Αλλαγή Ονόματος\n2. Αλλαγή Επωνύμου\n3. Αλλαγή Ηλικίας\n4. Αλλαγή Τηλεφώνου\n5. Επιστροφή\n");
        printf("Επιλογή πεδίου: ");
        fgets(temp, sizeof(temp), stdin); choice = atoi(temp);
        
        if (choice == 1) { printf("Νέο Όνομα: "); fgets(patients[idx].first_name, 31, stdin); strip_newline(patients[idx].first_name); }
        if (choice == 2) { printf("Νέο Επώνυμο: "); fgets(patients[idx].last_name, 31, stdin); strip_newline(patients[idx].last_name); }
        if (choice == 3) { printf("Νέα Ηλικία: "); fgets(temp, sizeof(temp), stdin); patients[idx].age = atoi(temp); }
        if (choice == 4) { printf("Νέο Τηλέφωνο: "); fgets(patients[idx].phone, 20, stdin); strip_newline(patients[idx].phone); }
    } while (choice != 5);
}

void delete_patient(Patient **patients, int *pat_count, Appointment **appointments, int *app_count) {
    char temp[100];
    printf("\n=== Διαγραφή Ασθενούς ===\n");
    printf("Δώστε ID Ασθενούς προς διαγραφή: "); fgets(temp, sizeof(temp), stdin);
    int id = atoi(temp);
    int idx = find_patient_idx(*patients, *pat_count, id);
    if (idx == -1) { printf("Ο ασθενής δεν υπάρχει.\n"); return; }

    int new_app_count = 0;
    for (int i = 0; i < *app_count; i++) {
        if ((*appointments)[i].patient_id != id) {
            (*appointments)[new_app_count] = (*appointments)[i];
            new_app_count++;
        }
    }
    if (new_app_count != *app_count) {
        *app_count = new_app_count;
        *appointments = realloc(*appointments, (*app_count) * sizeof(Appointment));
        printf("Ακυρώθηκαν αυτόματα τα ραντεβού του ασθενούς.\n");
    }

    for (int i = idx; i < *pat_count - 1; i++) (*patients)[i] = (*patients)[i + 1];
    (*pat_count)--;
    *patients = realloc(*patients, (*pat_count) * sizeof(Patient));
    printf("Ο ασθενής διαγράφηκε με επιτυχία.\n");
}

void search_patient_by_id(Patient *patients, int count) {
    char temp[100];
    printf("\n=== Αναζήτηση Ασθενούς βάσει ID ===\n");
    printf("Δώστε ID Αναζήτησης: "); fgets(temp, sizeof(temp), stdin);
    int idx = find_patient_idx(patients, count, atoi(temp));
    if (idx == -1) printf("Δεν βρέθηκε ο ασθενής.\n");
    else {
        printf("ID: %d | %s %s | Ηλικία: %d | Τηλέφωνο: %s\n", 
               patients[idx].patient_id, patients[idx].last_name, patients[idx].first_name, patients[idx].age, patients[idx].phone);
    }
}

int compare_patients(const void *a, const void *b) {
    Patient *p1 = (Patient *)a; Patient *p2 = (Patient *)b;
    if (patient_sort_criterion == 1) return p1->patient_id - p2->patient_id;
    if (patient_sort_criterion == 2) return strcmp(p1->last_name, p2->last_name);
    if (patient_sort_criterion == 3) return strcmp(p1->first_name, p2->first_name);
    return p1->age - p2->age;
}

void list_patients_sorted(Patient *patients, int count) {
    if (count == 0) { printf("Δεν υπάρχουν καταχωρημένοι ασθενείς.\n"); return; }
    char temp[100];
    printf("Επιλέξτε Κριτήριο Ταξινόμησης (1:ID, 2:Επώνυμο, 3:Όνομα, 4:Ηλικία): ");
    fgets(temp, sizeof(temp), stdin); patient_sort_criterion = atoi(temp);

    qsort(patients, count, sizeof(Patient), compare_patients);
    
    printf("\n=== Λίστα Ασθενών ===\n");
    printf("%-10s %-20s %-20s %-6s %-15s\n", "ID", "ΕΠΩΝΥΜΟ", "ΟΝΟΜΑ", "ΗΛΙΚΙΑ", "ΤΗΛΕΦΩΝΟ");
    printf("----------------------------------------------------------------------\n");
    for (int i = 0; i < count; i++) {
        printf("%-10d %-20s %-20s %-6d %-15s\n", patients[i].patient_id, patients[i].last_name, patients[i].first_name, patients[i].age, patients[i].phone);
    }
}

// ==============================================
// 4. ΕΝΟΤΗΤΑ: ΔΙΑΧΕΙΡΙΣΗ ΙΑΤΡΩΝ (Επιλογές 6-9)
// ==============================================

void add_doctor(Doctor **doctors, int *count) {
    Doctor d;
    char temp[100];
    printf("\n=== Προσθήκη Νέου Ιατρού ===\n");

    while (1) {
        printf("ID Ιατρού: "); fgets(temp, sizeof(temp), stdin); strip_newline(temp);
        if (is_numeric(temp)) {
            d.doctor_id = atoi(temp);
            if (find_doctor_idx(*doctors, *count, d.doctor_id) != -1) printf("Σφάλμα: Το ID υπάρχει ήδη.\n");
            else break;
        } else printf("Σφάλμα: Εισάγετε μόνο ψηφία.\n");
    }
    printf("Όνομα (max 30): "); fgets(d.first_name, 31, stdin); strip_newline(d.first_name);
    printf("Επώνυμο (max 30): "); fgets(d.last_name, 31, stdin); strip_newline(d.last_name);
    
    while (1) {
        printf("Ειδικότητα (0:GENERAL, 1:CARDIOLOGY, 2:PEDIATRICS, 3:ORTHOPEDICS, 4:NEUROLOGY, 5:OTHER): ");
        fgets(temp, sizeof(temp), stdin); int spec = atoi(temp);
        if (spec >= 0 && spec <= 5) { d.specialty = (Specialty)spec; break; }
        else printf("Σφάλμα: Επιλέξτε αριθμό από 0 έως 5.\n");
    }

    *doctors = realloc(*doctors, (*count + 1) * sizeof(Doctor));
    (*doctors)[*count] = d;
    (*count)++;
    printf("Ο ιατρός καταχωρήθηκε επιτυχώς!\n");
}

void edit_doctor(Doctor *doctors, int count) {
    char temp[100];
    printf("\n=== Επεξεργασία Ιατρού ===\n");
    printf("Δώστε ID Ιατρού: "); fgets(temp, sizeof(temp), stdin);
    int idx = find_doctor_idx(doctors, count, atoi(temp));
    if (idx == -1) { printf("Ο ιατρός δεν βρέθηκε.\n"); return; }

    int choice;
    do {
        printf("\nΚαρτέλα Ιατρού: Dr. %s %s\n", doctors[idx].last_name, doctors[idx].first_name);
        printf("1. Αλλαγή Ονόματος\n2. Αλλαγή Επωνύμου\n3. Αλλαγή Ειδικότητας\n4. Επιστροφή\n");
        printf("Επιλογή πεδίου: ");
        fgets(temp, sizeof(temp), stdin); choice = atoi(temp);
        
        if (choice == 1) { printf("Νέο Όνομα: "); fgets(doctors[idx].first_name, 31, stdin); strip_newline(doctors[idx].first_name); }
        if (choice == 2) { printf("Νέο Επώνυμο: "); fgets(doctors[idx].last_name, 31, stdin); strip_newline(doctors[idx].last_name); }
        if (choice == 3) { printf("Νέα Ειδικότητα (0-5): "); fgets(temp, sizeof(temp), stdin); doctors[idx].specialty = (Specialty)atoi(temp); }
    } while (choice != 4);
}

void delete_doctor(Doctor **doctors, int *doc_count, Appointment **appointments, int *app_count) {
    char temp[100];
    printf("\n=== Διαγραφή Ιατρού ===\n");
    printf("Δώστε ID Ιατρού προς διαγραφή: "); fgets(temp, sizeof(temp), stdin);
    int id = atoi(temp);
    int idx = find_doctor_idx(*doctors, *doc_count, id);
    if (idx == -1) { printf("Ο ιατρός δεν υπάρχει.\n"); return; }

    int new_app_count = 0;
    for (int i = 0; i < *app_count; i++) {
        if ((*appointments)[i].doctor_id != id) {
            (*appointments)[new_app_count] = (*appointments)[i];
            new_app_count++;
        }
    }
    if (new_app_count != *app_count) {
        *app_count = new_app_count;
        *appointments = realloc(*appointments, (*app_count) * sizeof(Appointment));
        printf("Ακυρώθηκαν αυτόματα τα ραντεβού του ιατρού.\n");
    }

    for (int i = idx; i < *doc_count - 1; i++) (*doctors)[i] = (*doctors)[i + 1];
    (*doc_count)--;
    *doctors = realloc(*doctors, (*doc_count) * sizeof(Doctor));
    printf("Ο ιατρός διαγράφηκε με επιτυχία.\n");
}

int compare_doctors(const void *a, const void *b) {
    Doctor *d1 = (Doctor *)a; Doctor *d2 = (Doctor *)b;
    if (d1->specialty != d2->specialty) return d1->specialty - d2->specialty;
    return strcmp(d1->last_name, d2->last_name);
}

void list_doctors_by_specialty(Doctor *doctors, int count) {
    if (count == 0) { printf("Δεν υπάρχουν καταχωρημένοι ιατροί.\n"); return; }
    
    qsort(doctors, count, sizeof(Doctor), compare_doctors);
    
    printf("\n=== Λίστα Ιατρών ανά Ειδικότητα ===\n");
    printf("%-10s %-20s %-20s %-15s\n", "ID", "ΕΠΩΝΥΜΟ", "ΟΝΟΜΑ", "ΕΙΔΙΚΟΤΗΤΑ");
    printf("----------------------------------------------------------------------\n");
    for (int i = 0; i < count; i++) {
        printf("%-10d %-20s %-20s %-15s\n", doctors[i].doctor_id, doctors[i].last_name, doctors[i].first_name, SPECIALTY_NAMES[doctors[i].specialty]);
    }
}

// ===================================================
// 5. ΕΝΟΤΗΤΑ: ΔΙΑΧΕΙΡΙΣΗ ΡΑΝΤΕΒΟΥ (Επιλογές 10-13)
// ===================================================

void book_appointment(Appointment **appointments, int *app_count, Patient *patients, int pat_count, Doctor *doctors, int doc_count) {
    Appointment app;
    char temp[100];
    printf("\n=== Κράτηση Ραντεβού ===\n");

    printf("ID Ασθενούς: "); fgets(temp, sizeof(temp), stdin); app.patient_id = atoi(temp);
    if (find_patient_idx(patients, pat_count, app.patient_id) == -1) { printf("Σφάλμα: Ο ασθενής δεν υπάρχει.\n"); return; }

    printf("ID Ιατρού: "); fgets(temp, sizeof(temp), stdin); app.doctor_id = atoi(temp);
    if (find_doctor_idx(doctors, doc_count, app.doctor_id) == -1) { printf("Σφάλμα: Ο ιατρός δεν υπάρχει.\n"); return; }

    while (1) {
        printf("Ημερομηνία (YYYY-MM-DD): "); fgets(app.date, sizeof(app.date), stdin); strip_newline(app.date);
        if (is_working_day(app.date)) break;
        printf("Σφάλμα: Μόνο εργάσιμες ημέρες (Δευτέρα-Παρασκευή).\n");
    }

    while (1) {
        printf("Ώρα (HH:MM π.χ. 08:00, 14:30): "); fgets(app.time, sizeof(app.time), stdin); strip_newline(app.time);
        if (!is_valid_time_slot(app.time)) { printf("Σφάλμα: Εκτός ωραρίου κλινικής (08:00-15:00 ανά 30').\n"); continue; }
        if (!is_slot_busy(*appointments, *app_count, app.doctor_id, app.patient_id, app.date, app.time)) break;
    }

    *appointments = realloc(*appointments, (*app_count + 1) * sizeof(Appointment));
    (*appointments)[*app_count] = app;
    (*app_count)++;
    printf("Το ραντεβού κλείστηκε με επιτυχία!\n");
}

void cancel_appointment(Appointment **appointments, int *app_count) {
    char temp[100], date[11], time[6];
    int pat_id, doc_id;
    printf("\n=== Ακύρωση Ραντεβού ===\n");
    
    printf("ID Ασθενούς: "); fgets(temp, sizeof(temp), stdin); pat_id = atoi(temp);
    printf("ID Ιατρού: "); fgets(temp, sizeof(temp), stdin); doc_id = atoi(temp);
    printf("Ημερομηνία (YYYY-MM-DD): "); fgets(date, sizeof(date), stdin); strip_newline(date);
    printf("Ώρα (HH:MM): "); fgets(time, sizeof(time), stdin); strip_newline(time);

    int idx = -1;
    for (int i = 0; i < *app_count; i++) {
        if ((*appointments)[i].patient_id == pat_id && (*appointments)[i].doctor_id == doc_id &&
            strcmp((*appointments)[i].date, date) == 0 && strcmp((*appointments)[i].time, time) == 0) {
            idx = i; break;
        }
    }
    if (idx == -1) { printf("Το ραντεβού δεν βρέθηκε.\n"); return; }

    for (int i = idx; i < *app_count - 1; i++) (*appointments)[i] = (*appointments)[i + 1];
    (*app_count)--;
    *appointments = realloc(*appointments, (*app_count) * sizeof(Appointment));
    printf("Το ραντεβού ακυρώθηκε επιτυχώς.\n");
}

void reschedule_appointment(Appointment *appointments, int app_count) {
    char temp[100], old_date[11], old_time[6];
    int pat_id, doc_id;
    printf("\n=== Επαναπρογραμματισμός Ραντεβού ===\n");
    
    printf("ID Ασθενούς: "); fgets(temp, sizeof(temp), stdin); pat_id = atoi(temp);
    printf("ID Ιατρου: "); fgets(temp, sizeof(temp), stdin); doc_id = atoi(temp);
    printf("Τρέχουσα Ημερομηνία (YYYY-MM-DD): "); fgets(old_date, sizeof(old_date), stdin); strip_newline(old_date);
    printf("Τρέχουσα Ώρα (HH:MM): "); fgets(old_time, sizeof(old_time), stdin); strip_newline(old_time);

    int idx = -1;
    for (int i = 0; i < app_count; i++) {
        if (appointments[i].patient_id == pat_id && appointments[i].doctor_id == doc_id &&
            strcmp(appointments[i].date, old_date) == 0 && strcmp(appointments[i].time, old_time) == 0) {
            idx = i; break;
        }
    }
    if (idx == -1) { printf("Το ραντεβού δεν βρέθηκε.\n"); return; }

    char new_date[11], new_time[6];
    while (1) {
        printf("Νέα Ημερομηνία (YYYY-MM-DD): "); fgets(new_date, sizeof(new_date), stdin); strip_newline(new_date);
        if (is_working_day(new_date)) break;
        printf("Σφάλμα: Μόνο εργάσιμες ημέρες.\n");
    }
    while (1) {
        printf("Νέα Ώρα (HH:MM): "); fgets(new_time, sizeof(new_time), stdin); strip_newline(new_time);
        if (!is_valid_time_slot(new_time)) continue;
        
        appointments[idx].doctor_id = -1; 
        bool busy = is_slot_busy(appointments, app_count, doc_id, pat_id, new_date, new_time);
        appointments[idx].doctor_id = doc_id;
        if (!busy) break;
    }
    strcpy(appointments[idx].date, new_date);
    strcpy(appointments[idx].time, new_time);
    printf("Το ραντεβού επαναπρογραμματίστηκε με επιτυχία.\n");
}

int compare_times(const void *a, const void *b) {
    return strcmp(((Appointment *)a)->time, ((Appointment *)b)->time);
}

void display_day_agenda(Appointment *appointments, int app_count, Patient *patients, int pat_count, Doctor *doctors, int doc_count) {
    char target_date[11];
    printf("\n=== Ημερήσια Ατζέντα Ραντεβού ===\n");
    printf("Εισάγετε Ημερομηνία (YYYY-MM-DD): "); fgets(target_date, sizeof(target_date), stdin); strip_newline(target_date);

    qsort(appointments, app_count, sizeof(Appointment), compare_times);

    printf("\nΑΤΖΕΝΤΑ ΚΛΙΝΙΚΗΣ ΓΙΑ ΤΗΝ ΗΜΕΡΟΜΗΝΙΑ: %s\n", target_date);
    printf("%-7s | %-25s | %-25s\n", "ΩΡΑ", "ΙΑΤΡΟΣ", "ΑΣΘΕΝΗΣ");
    printf("----------------------------------------------------------------------\n");

    int found = 0;
    for (int i = 0; i < app_count; i++) {
        if (strcmp(appointments[i].date, target_date) == 0) {
            found = 1;
            int d_idx = find_doctor_idx(doctors, doc_count, appointments[i].doctor_id);
            int p_idx = find_patient_idx(patients, pat_count, appointments[i].patient_id);
            char d_name[60], p_name[60];
            sprintf(d_name, "Dr. %s", (d_idx != -1) ? doctors[d_idx].last_name : "Unknown");
            sprintf(p_name, "%s", (p_idx != -1) ? patients[p_idx].last_name : "Unknown");
            printf("%-7s | %-25s | %-25s\n", appointments[i].time, d_name, p_name);
        }
    }
    if (!found) printf("Δεν υπάρχουν προγραμματισμένα ραντεβού για αυτή τη μέρα.\n");
}
//Συναρτηση εξοδου προς το μενου επιλογων
void wait_for_user() {
    printf("\nΠίεσε [Enter] για επιστροφή στο μενού...");
    fflush(stdout);
    int c;
    while ((c = getchar()) != '\n' && c != EOF); 
    getchar(); 
}

// ====================
// 6. ΚΥΡΙΑ ΣΥΝΑΡΤΗΣΗ
// ====================

int main() {
   int patient_count = 10;
    Patient *patients = malloc(patient_count * sizeof(Patient));
    
    patients[0] = (Patient){10001, "Alice", "Smith", 30, "6911111111"};
    patients[1] = (Patient){10002, "Jones", "Carol", 42, "6922222222"};
    patients[2] = (Patient){10003, "Carol", "Williams", 25, "6933333333"};
    patients[3] = (Patient){10004, "David", "Brown", 56, "6944444444"};
    patients[4] = (Patient){10005, "Davis", "Frank", 61, "6955555555"};
    patients[5] = (Patient){10006, "Frank", "Miller", 38, "6966666666"};
    patients[6] = (Patient){10007, "Grace", "Wilson", 29, "6977777777"};
    patients[7] = (Patient){10008, "Henry", "Moore", 48, "6988888888"};
    patients[8] = (Patient){10009, "Iris", "Taylor", 34, "6999999999"};
    patients[9] = (Patient){10010, "John", "Anderson", 50, "6900000000"};

    // 2. ΑΡΧΙΚΟΠΟΙΗΣΗ & ΠΡΟΣΘΗΚΗ ΙΑΤΡΩΝ (Από το doctors.dat)
    // Ειδικότητες: 0:GENERAL, 1:CARDIOLOGY, 2:PEDIATRICS, 3:ORTHOPEDICS, 4:NEUROLOGY, 5:OTHER
    int doctor_count = 5;
    Doctor *doctors = malloc(doctor_count * sizeof(Doctor));
    
    doctors[0] = (Doctor){1, "Gregory", "House", NEUROLOGY};   // ID 1
    doctors[1] = (Doctor){2, "Lisa", "Cuddy", GENERAL};         // ID 2
    doctors[2] = (Doctor){3, "Allison", "Cameron", CARDIOLOGY}; // ID 3
    doctors[3] = (Doctor){4, "Robert", "Chase", ORTHOPEDICS};   // ID 4
    doctors[4] = (Doctor){5, "Eric", "Foreman", OTHER};        // ID 5

    // 3. ΑΡΧΙΚΟΠΟΙΗΣΗ & ΠΡΟΣΘΗΚΗ ΡΑΝΤΕΒΟΥ (Από το appointments.txt)
    int appointment_count = 12;
    Appointment *appointments = malloc(appointment_count * sizeof(Appointment));
    
    appointments[0]  = (Appointment){10001, 1, "2026-05-11", "09:00"};
    appointments[1]  = (Appointment){10003, 2, "2026-05-11", "09:30"};
    appointments[2]  = (Appointment){10005, 3, "2026-05-11", "10:00"};
    appointments[3]  = (Appointment){10010, 3, "2026-05-11", "10:30"};
    appointments[4]  = (Appointment){10002, 2, "2026-05-12", "09:00"};
    appointments[5]  = (Appointment){10004, 4, "2026-05-12", "11:00"};
    appointments[6]  = (Appointment){10006, 5, "2026-05-13", "08:30"};
    appointments[7]  = (Appointment){10007, 1, "2026-05-13", "14:00"};
    appointments[8]  = (Appointment){10008, 4, "2026-05-14", "09:30"};
    appointments[9]  = (Appointment){10009, 1, "2026-05-14", "11:30"};
    appointments[10] = (Appointment){10001, 2, "2026-05-15", "08:00"};
    appointments[11] = (Appointment){10005, 3, "2026-05-15", "14:30"};

    char choice_str[10];
    int choice;

    do {
	system("clear");
        printf("\n=== HOSPITAL MANAGEMENT MENU ===\n");
 	printf("Patients:\n");
        printf("	1. Add patient\n");
        printf("	2. Edit patient\n");
        printf("	3. Delete patient\n");
        printf("	4. Search patient by ID\n");
        printf("	5. List patients (sorted)\n");
	printf("Doctors:\n");
        printf("	6. Add doctor\n");
        printf("	7. Edit doctor\n");
        printf("	8. Delete doctor\n");
        printf("	9. List doctors sorted by specialty\n");
        printf("Appointments:\n");
	printf("	10. Book appointment\n");
        printf("	11. Cancel appointment\n");
        printf("	12. Reschedule appointment\n");
        printf("	13. Display day agenda\n");
        printf("	0. Save and Exit Process\n");
        printf("Choose option: ");

        if (fgets(choice_str, sizeof(choice_str), stdin) == NULL) break;
        choice = atoi(choice_str);

switch (choice) {
            case 1:  
                add_patient(&patients, &patient_count); 
                wait_for_user(); 
                break;
            case 2:  
                edit_patient(patients, patient_count); 
                wait_for_user(); 
                break; 
            case 3:  
                delete_patient(&patients, &patient_count, &appointments, &appointment_count); 
                wait_for_user(); 
                break;
            case 4:  
                search_patient_by_id(patients, patient_count); 
                wait_for_user(); 
                break;
            case 5:  
                list_patients_sorted(patients, patient_count); 
                wait_for_user(); 
                break; 
            case 6:  
                add_doctor(&doctors, &doctor_count); 
                wait_for_user(); 
                break;
            case 7:  
                edit_doctor(doctors, doctor_count); 
                wait_for_user(); 
                break; 
            case 8:  
                delete_doctor(&doctors, &doctor_count, &appointments, &appointment_count); 
                wait_for_user(); 
                break;
            case 9:  
                list_doctors_by_specialty(doctors, doctor_count); 
                wait_for_user(); 
                break; 
            case 10: 
                book_appointment(&appointments, &appointment_count, patients, patient_count, doctors, doctor_count); 
                wait_for_user(); 
                break;
            case 11: 
                cancel_appointment(&appointments, &appointment_count); 
                wait_for_user(); 
                break;
            case 12: 
                reschedule_appointment(appointments, appointment_count); 
                wait_for_user(); 
                break;
            case 13: 
                display_day_agenda(appointments, appointment_count, patients, patient_count, doctors, doctor_count); 
                wait_for_user(); 
                break; 
            case 0:  
                system("clear");
                printf("Έξοδος από την εφαρμογή. Καλή σας μέρα!\n"); 
                break;
            default: 
                printf("Μη έγκυρη επιλογή. "); 
                wait_for_user();
        }
    }while (choice != 0);

    free(patients);
    free(doctors);
    free(appointments);
    return 0;
}
