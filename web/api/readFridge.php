#!/usr/bin/php

<?php

  //Include db connect class
  require_once __DIR__ . '/db_connect.php';

  $db = new Db();

  //Create an array for JSON response
  $response = array();
  $query = "SELECT * FROM recipez_database.Fridge";
  $resultQuery = $db->query($query) or die($db->error());

  while ($rowResults = $resultQuery->fetch_assoc()) {
    $response[] = $rowResults;
  }

  //echo json_encode($emparray);
  $filepath = './json/readFridge.json';
  $fp = fopen($filepath, 'w');
  fwrite($fp, json_encode($response));
  fclose($fp);
  chmod($filepath, 0777);

//  mysqli_close($conn);

?>
