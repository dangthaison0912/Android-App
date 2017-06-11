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


  $recipe_name = $decoded[Recipe_Name];
  $date = $decoded[Expire_Date];


  //Q uery to insert entry to table Recipe
  $queryRecipe = "INSERT INTO recipez_database.Fridge (Food, Expiry_Date) VALUES (\"$recipe_name\", STR_TO_DATE(\"$date\", '%d/%m/%Y'))";
  $result = $db->queryInsert($queryRecipe) or die($db->error());

?>
