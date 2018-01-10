<?php
	include"connect.php";
	$history_id = $_GET['history_id'];
	$sql = "select * from history where history_id='$history_id'";
	if($stmt = $db->query($sql))
	{
		while($result=mysqli_fetch_object($stmt))
		{		
		echo json_encode($result);
		}
	}
	//echo '{"history_id":"00","result":"很棒","resultURL":"http://114.42.108.178/result/00.jpg","date":"2018-01-06"}';
?>