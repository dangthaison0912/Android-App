/* For getting recipes with a particular ingredient (eg.chicken) */

SELECT recipez_database.Recipe.Recipe_ID, Name, Ingredients, Amount, Steps
FROM recipez_database.Recipe
INNER JOIN recipez_database.Ingredients
ON recipez_database.Recipe.Recipe_ID=recipez_database.Ingredients.Recipe_ID
WHERE Ingredients='chicken';


/* Search for recipes where recipe names or ingredients includes 'chicken' */
SELECT DISTINCT recipez_database.Recipe.Recipe_ID, Name
FROM recipez_database.Recipe
INNER JOIN recipez_database.Ingredients
ON recipez_database.Recipe.Recipe_ID=recipez_database.Ingredients.Recipe_ID
WHERE Ingredients LIKE '%chicken%'
OR Name LIKE '%chicken%';


/* Get picture and name of the recipes which contains word 'chicken' */
SELECT recipez_database.Recipe.Recipe_ID, Picture, Name
FROM recipez_database.Pictures
INNER JOIN recipez_database.Recipe
ON recipez_database.Recipe.Recipe_ID=recipez_database.Pictures.Recipe_ID
WHERE recipez_database.Recipe.Recipe_ID
IN 
(SELECT DISTINCT recipez_database.Recipe.Recipe_ID
FROM recipez_database.Recipe
INNER JOIN recipez_database.Ingredients
ON recipez_database.Recipe.Recipe_ID=recipez_database.Ingredients.Recipe_ID
WHERE Ingredients LIKE '%chicken%'
OR Name LIKE '%chicken%');

/* Get the ingredients and amount, (given Recipe_ID, Name) */
SELECT recipez_database.Recipe.Recipe_ID, Name, Ingredients, Amount
FROM recipez_database.Ingredients
INNER JOIN recipez_database.Recipe
ON recipez_database.Recipe.Recipe_ID=recipez_database.Ingredients.Recipe_ID
WHERE recipez_database.Recipe.Recipe_ID='50' 
AND recipez_database.Recipe.Name='test';

/* Get the steps */
SELECT Recipe_ID, Name, Steps
FROM recipez_database.Recipe
WHERE Recipe_ID='50' 
AND Name='test';
