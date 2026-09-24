-- 1) Συνταγές συγκεκριμένης περιοχής με πάνω από 5 υλικά
SELECT R.RNAME
FROM RECIPE R
JOIN COUNTRY C ON R.COUNTRY_ID = C.COUNTRY_ID
JOIN RECIPE_INGREDIENTS RI ON R.RECIPE_ID = RI.RECIPE_ID
WHERE C.NAME = 'Ελλάδα'
GROUP BY R.RECIPE_ID, R.RNAME
HAVING COUNT(RI.INGREDIENT_ID) > 5;

-- 2) Οι 3 συνταγές με τα περισσότερα υλικά
SELECT * FROM (
    SELECT R.RNAME, COUNT(RI.INGREDIENT_ID) as total_ingredients
    FROM RECIPE R
    JOIN RECIPE_INGREDIENTS RI ON R.RECIPE_ID = RI.RECIPE_ID
    GROUP BY R.RECIPE_ID, R.RNAME
    ORDER BY total_ingredients DESC
) WHERE ROWNUM <= 3;

-- 3) Συνταγές για φούρνο (oven) 
SELECT RNAME, INSTRUCTION
FROM RECIPE
WHERE UPPER(INSTRUCTION) LIKE '%OVEN%' OR UPPER(INSTRUCTION) LIKE '%ΦΟΥΡΝΟ%';

-- 4) Τηγανητές συνταγές (fried/fry)
SELECT RNAME, INSTRUCTION
FROM RECIPE
WHERE UPPER(INSTRUCTION) LIKE '%FRIED%' 
   OR UPPER(INSTRUCTION) LIKE '%FRY%' 
   OR UPPER(INSTRUCTION) LIKE '%ΤΗΓΑΝΗΤΕΣ%';

-- 5) Μέσος αριθμός υλικών στις συνταγές ανά περιοχή
SELECT C.NAME as Region, AVG(ing_count) as Avg_Ingredients
FROM (
    SELECT R.COUNTRY_ID, R.RECIPE_ID, COUNT(RI.INGREDIENT_ID) as ing_count
    FROM RECIPE R
    JOIN RECIPE_INGREDIENTS RI ON R.RECIPE_ID = RI.RECIPE_ID
    GROUP BY R.COUNTRY_ID, R.RECIPE_ID
) t
JOIN COUNTRY C ON t.COUNTRY_ID = C.COUNTRY_ID
GROUP BY C.NAME;

-- 6) Οι 2 πιο δημοφιλείς περιοχές (βάσει πλήθους συνταγών)
SELECT * FROM (
    SELECT C.NAME, COUNT(R.RECIPE_ID) as recipe_count
    FROM COUNTRY C
    JOIN RECIPE R ON C.COUNTRY_ID = R.COUNTRY_ID
    GROUP BY C.NAME
    ORDER BY recipe_count DESC
) WHERE ROWNUM <= 2;

-- 7) Συνταγές ελληνικής κουζίνας
SELECT R.RNAME
FROM RECIPE R
JOIN COUNTRY C ON R.COUNTRY_ID = C.COUNTRY_ID
WHERE C.NAME = 'Ελλάδα';

-- 8) Υλικά που εμφανίζονται σε πάνω από 5 συνταγές
SELECT I.INAME, COUNT(RI.RECIPE_ID) as appearances
FROM INGREDIENT I
JOIN RECIPE_INGREDIENTS RI ON I.INGREDIENT_ID = RI.INGREDIENT_ID
GROUP BY I.INGREDIENT_ID, I.INAME
HAVING COUNT(RI.RECIPE_ID) > 5;

-- 9) Οι ιστότοποι (domain names) με τις πιο πολλές συνταγές
SELECT SUBSTR(SOURCE_URL, INSTR(SOURCE_URL, '//') + 2, 
       INSTR(SOURCE_URL, '/', INSTR(SOURCE_URL, '//') + 2) - (INSTR(SOURCE_URL, '//') + 2)) as Domain,
       COUNT(*) as Recipe_Count
FROM RECIPE
WHERE SOURCE_URL IS NOT NULL
GROUP BY SUBSTR(SOURCE_URL, INSTR(SOURCE_URL, '//') + 2, 
       INSTR(SOURCE_URL, '/', INSTR(SOURCE_URL, '//') + 2) - (INSTR(SOURCE_URL, '//') + 2))
ORDER BY Recipe_Count DESC;

-- 10) Ποιοι χρήστες έχουν καταχωρήσει προτάσεις τροποποίησης υλικών και σε ποιες συνταγές αναφέρονται οι προτάσεις τους
SELECT U.NAME, U.LNAME, R.RNAME as Recipe, I.INAME as Ingredient, M.MODIFICATION
FROM USERS_MODIFY M
JOIN APP_USER U ON M.EMAIL = U.EMAIL
JOIN RECIPE R ON M.RECIPE_ID = R.RECIPE_ID
JOIN INGREDIENT I ON M.INGREDIENT_ID = I.INGREDIENT_ID;