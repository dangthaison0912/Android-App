#!/usr/bin/php

<?php

  //Include db connect class
  require_once __DIR__ . '/db_connect.php';

  $db = new Db();


  //Create an array for JSON response
  //$response = array();

  $contentType = isset($_SERVER["CONTENT_TYPE"]) ? trim($_SERVER["CONTENT_TYPE"]) : '';
  if(strcasecmp($contentType, 'application/json') != 0){
      throw new Exception('Content type must be: application/json');
  }

  //Retrieve the contents from POST methond
  $content = file_get_contents("php://input");

  //Decode the json into readable PHP object
  $decoded = json_decode($content, TRUE);


  $recipe_name = $decoded[Recipe_name];
  $steps = $decoded[Steps];

  $jsonIterator = new RecursiveIteratorIterator(
      new RecursiveArrayIterator($decoded[Ingredients]),
      RecursiveIteratorIterator::SELF_FIRST);

  $ingredients = array();
  $amount = array();

  foreach ($jsonIterator as $key => $val) {
    if(!is_array($val)) {
      $ingredients[] = $key;
      $amounts[] = $val;
    }
  }

  //Query to insert entry to table Recipe
  $queryRecipe = "INSERT INTO recipez_database.Recipe (Name, Steps) VALUES (\"$recipe_name\", \"$steps\")";
  $last_id = $db->queryInsert($queryRecipe) or die($db->error());

  $noOfIngredients = count($ingredients);

  //Query to insert entries to table Ingredients
  for ($count = 0; $count < $noOfIngredients; $count++) {
      $queryIngredients = "INSERT INTO recipez_database.Ingredients (Recipe_ID, Ingredients, Amount) VALUES ($last_id, \"$ingredients[$count]\", $amounts[$count])";
      $resultQuery = $db->query($queryIngredients) or die($db->error());
  }




?>
