<?php
	include"connect.php";
	
	$device_mac = $_GET['device_mac'];
	$user_id = $_GET['user_id'];
	
	$sql = "insert into device (device_mac, user_id) values ('$device_mac','$user_id')";
	if(mysqli_query($db,$sql))
	{
		//找此裝置的ID
		$findID = "select * from device where device_mac='$device_mac'";
		$result = mysqli_fetch_object($db->query($findID));
		echo $result->device_id;
	}
	else
	{
		$findID = "select * from device where device_mac='$device_mac'";
		$result = mysqli_fetch_object($db->query($findID));
		echo $result->device_id;
	}
	
?>