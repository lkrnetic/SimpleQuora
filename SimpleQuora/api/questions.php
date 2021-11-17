<?php
include("connection.php");
$db = new dbObj();
$connection =  $db->getConnstring();
$request_method=$_SERVER["REQUEST_METHOD"];
function get_questions()
	{
		global $connection;
		$query="SELECT id,question_text FROM questions";
		$response=array();
		$result=mysqli_query($connection, $query);
		while($row=mysqli_fetch_array($result))
		{
			$response[] = array("id" => $row["id"], "question_text" => $row["question_text"]);
			//$response[$row['id'] ] = $row['question_text'];
			//$response["id"] = $row['id'];
			//$response["question_text"] = $row['question_text'];
			//$response[$row['id'],$row["question"]];
			//$response["id"] = $row['id'];
			//$response["question_text"] = $row['question_text'];
			//$response[]=$row;
		}
		header('Content-Type: application/json');
		$questions = array();
		$questions[] = array("status"=>"ok","questions" => $response); 
		echo json_encode($questions);
	}
function get_answers($id)
{
	global $connection;
	$query="SELECT username,answer_text  FROM answer WHERE question_id = '".$id."'";
		$response=array();
	$result=mysqli_query($connection, $query);
	while($row=mysqli_fetch_array($result))
	{
		$response[] = array("username" => $row["username"], "answer_text" => $row["answer_text"]);
	}
	header('Content-Type: application/json');
	$answers = array();
	$answers[] = array("status"=>"ok","answers" => $response); 
	echo json_encode($answers);
}
function get_question($id)
{
	global $connection;
	$query="SELECT question_text FROM questions WHERE id = '".$id."'";
	$response=array();
	$result=mysqli_query($connection, $query);
	while($row=mysqli_fetch_array($result))
	{
		$response[] = array("id" => $row["id"], "question_text" => $row["question_text"]);
	}
	header('Content-Type: application/json');
	$questions = array();
	$questions[] = array("status"=>"ok","questions" => $response); 
	echo json_encode($questions);
}
function insert_question()
	{
		global $connection;
		$res = file_get_contents('php://input');
		$obj = json_decode($res); 
		$question_text = $obj->{'question_text'};
		$user_id = $obj->{'user_id'};
		$query = "INSERT INTO `questions`(`question_text`,`user_id`) VALUES ('".$question_text."', '".$user_id."')";
		if(mysqli_query($connection, $query))
		{
			$response=array(
				'status' => 1,
				'status_message' =>'Employee Added Successfully.'
			);
		}
		else
		{
			$response=array(
				'status' => 0,
				'status_message' =>'Employee Addition Failed.'
			);
		}
		$response=array(
			'user_id' => $user_id,
			'question_text' =>$question_text
		);
		header('Content-Type: application/json');
		echo json_encode($response);	
		
	}
function insert_answer()
{
	global $connection;
	$res = file_get_contents('php://input');
	//echo $res;
	$obj = json_decode($res); 
	$answer_text = $obj->{'answer_text'};
	$username = $obj->{'username'};
	$user_id = $obj->{'user_id'};
	$question_id = $obj->{'question_id'};
	$query = "INSERT INTO `answer`(`question_id`,`answer_text`,`user_id`,`username`) VALUES ('".$question_id."','".$answer_text."','".$user_id."','".$username."')";
	//echo $query;
	
	if(mysqli_query($connection, $query))
	{
		$response=array(
			'status' => 1,
			'status_message' =>'Answer Added Successfully.'
		);
	}
	else
	{
		$response=array(
			'status' => 0,
			'status_message' =>'Answer Addition Failed.'
		);
	}
	$response=array(
			'answer_text' => $answer_text,
			'user_id' =>$user_id,
			'question_id' =>$question_id,
			'username' =>$username,
		);
	header('Content-Type: application/json');
	echo json_encode($response);
	
}
switch($request_method)
	{
		case 'GET':
			// Retrive questions
			if (strpos($_SERVER['REQUEST_URI'], "/answers") !== false){
				$id=intval($_GET["id"]);
				get_answers($id);
			}
			else if(!empty($_GET["id"]))
			{
				$id=intval($_GET["id"]);
				get_question($id);
			}
			else
			{
				get_questions();
			}
			break;
		case 'POST':
		// Insert Product
		if (strpos($_SERVER['REQUEST_URI'], "/answer") !== false){
			insert_answer();
		}
		else if(strpos($_SERVER['REQUEST_URI'], "/pitanje") !== false){
			insert_question();
		}
		break;
		default:
			// Invalid Request Method
			header("HTTP/1.0 405 Method Not Allowed");
			break;
	}
?>