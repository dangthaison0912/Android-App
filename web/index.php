#!/usr/bin/php
<?php
  $servername = "recipezdb.c2orxitvgolk.eu-west-2.rds.amazonaws.com";
  $username = "recipez123";
  $password = "recipezgroup10";
  $database = "recipez_database";
  $port = "3306";
  // Create connection
  
  $conn = new mysqli($servername, $username, $password, $database, $port);

  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }
  //echo "Connected successfully";

  $emparray = array();
  $query = "SELECT * FROM recipez_database.Fridge";
  $resultQuery = $conn->query($query);
  while ($rowResults = $resultQuery->fetch_assoc()) {
    $emparray[] = $rowResults;
  }

  //echo json_encode($emparray);
  $filepath = './json/empdata.json';
  $fp = fopen($filepath, 'w');
  fwrite($fp, json_encode($emparray));
  fclose($fp);
  chmod($filepath, 0777);

  mysqli_close($conn);

?>
