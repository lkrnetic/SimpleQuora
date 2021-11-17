<?php
include("connection.php");
$db = new dbObj();
$connection =  $db->getConnstring();
$request_method=$_SERVER["REQUEST_METHOD"];
function login()
{
	global $connection;
	$res = file_get_contents('php://input');
	//echo $res;
	$obj = json_decode($res); 
	$username = $obj->{'username'};
	$password = $obj->{'password'};
	$response=array();
	$id = -1;
	$query="SELECT username, password, id FROM user WHERE username = '".$username."' AND password = '".$password."'";
	//echo $query;	
	$result=mysqli_query($connection, $query);
	while($row=mysqli_fetch_array($result)){
		$id = $row["id"];
	}
	if($id != -1){
		$response=array(
			'status' => 1,
			'id' => $id,
			'username' => $username,
			'password' => $password		
		);
	}
	else {
		$response=array(
			'status' => -1,
			'id' => '-1',
			'username' => '',
			'password' => ''	
		);		
	}
	header('Content-Type: application/json');
	echo json_encode($response);
}

function registerUser()
{
	global $connection;
	$res = file_get_contents('php://input');
	//echo $res;
	$obj = json_decode($res); 
	$username = $obj->{'username'};
	$password = $obj->{'password'};
	$response=array();
	//$user_id = $_POST["user_id"];
	//$query = "INSERT INTO `answer`(`question_id`,`answer_text`,`user_id`,`username`) VALUES ('".$question_id."','".$answer_text."','3','".$username."')";
	$query="SELECT username, password, id FROM user WHERE username = '".$username."'";
	$result=mysqli_query($connection, $query);
	$brojac = 0;
	while($row=mysqli_fetch_array($result)){
		$id = $row["id"];
		if(strcmp($row["username"],$username) == 0){
			$brojac++;
		}
	}
	if($brojac == 0){
		$query = "INSERT INTO `user`(`username`,`password`) VALUES ('".$username."','".$password."')";	
		if(mysqli_query($connection, $query)){
			$query="SELECT username, password, id FROM user WHERE username = '".$username."' AND password = '".$password."'";
			$result=mysqli_query($connection, $query);
			while($row=mysqli_fetch_array($result)){
				$id = $row["id"];
			}
			$response=array(
				'status' => 1,
				'id' => $id,
				'username' => $username,
				'password' => $password		
			);
		}		
	}
	else
	{
		$response=array(
			'status' => -1,
			'id' => '-1',
			'username' => '',
			'password' => ''	
		);
	}
	header('Content-Type: application/json');
	echo json_encode($response);
	
}


switch($request_method)
	{	
		case 'POST':
			if (strpos($_SERVER['REQUEST_URI'], "/registration") !== false){
				registerUser();
			}
			else if(strpos($_SERVER['REQUEST_URI'], "/login") !== false){
				login();
			}
		break;
		default:
			// Invalid Request Method
			header("HTTP/1.0 405 Method Not Allowed");
			break;
	}
?>