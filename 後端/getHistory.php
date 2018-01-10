<?php
	include"connect.php";
	
	$user_id = $_GET['user_id'];
	$sql = "select * from history where user_id='$user_id'";
	$count = 0;
	if($stmt = $db->query($sql))
	{
		echo "[";
		while($result=mysqli_fetch_object($stmt))
		{		
			if($count == 0)
				echo '{"history_id":"'.$result->history_id.'","result":"'.$result->result.'","resultURL":"'.$result->resultURL.'","date":"'.$result->date.'"}';
			else
				echo ',{"history_id":"'.$result->history_id.'","result":"'.$result->result.'","resultURL":"'.$result->resultURL.'","date":"'.$result->date.'"}';
			$count++;
		}
		echo "]";
	}
	//echo '[{"history_id":"00","result":"很棒","resultURL":"http://114.42.108.178/result/00.jpg","date":"2018-01-06"},{"history_id":"01","result":"太糟了","resultURL":"http://114.42.108.178/result/01.jpg","date":"2018-01-08"}]';
?>