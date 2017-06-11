<?php

  Class Db {
    static $conn;

    //Establish connection with MySQL database
    public function connect() {
      if (!isset(self::$conn)) {
        $config = parse_ini_file('../../config.ini');

        self::$conn = new mysqli($config['server'], $config['username'], $config['password'], $config['dbname'], $config['port']);
      }

      if (self::$conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
      }

      return self::$conn;
    }

    //Given a query, perform the query on the database and return the results.
    public function query($query) {
      $conn = $this->connect();

      $result = $conn->query($query);

      return $result;
    }

    //Given an INSERT INTO query, perform the query on the database and return the value of auto-incremented value
    public function queryInsert($query) {
      $conn = $this->connect();

      $result = $conn->query($query);

      $id = $conn->insert_id;

      return $id;
    }

    //Given a SELECT queery, perform the query on the database and return the results in array.
    public function select($query) {
  		$rows = array();
  		$result = $this->query($query);
  		if($result === false) {
  			return false;
  		}
  		while ($row = $result->fetch_assoc()) {
  			$rows[] = $row;
  		}
  		return $rows;
  	}

    //Returns the error message when error occurs
    public function error() {
      $conn = $this->connect();
      return $conn->error;
    }

  }

?>
