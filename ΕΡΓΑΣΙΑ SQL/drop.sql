-- 1. Διαγραφή πινάκων που έχουν ξένα κλειδιά προς άλλους πίνακες
DROP TABLE USERS_MODIFY;
DROP TABLE USER_FAVORITES;
DROP TABLE RECIPE_INGREDIENTS;

-- 2. Διαγραφή του πίνακα RECIPE (εξαρτάται από CATEGORY και COUNTRY)
DROP TABLE RECIPE;

-- 3. Διαγραφή των πινάκων "γονέων" που δεν έχουν πλέον εξαρτήσεις
DROP TABLE APP_USER;
DROP TABLE INGREDIENT;
DROP TABLE CATEGORY;
DROP TABLE COUNTRY;
