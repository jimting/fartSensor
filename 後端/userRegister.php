<?php
	include"connect.php";
	
	$user_id = $_GET['user_id'];
	$sql = "insert into user values ('$user_id')";
	if(mysqli_query($db,$sql))
		echo "註冊成功！歡迎使用！";
	else
		echo "歡迎你！";
	
?>