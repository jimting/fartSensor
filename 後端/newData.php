<?php
	include"connect.php";
	
	$P = $_GET['P'];		//烷類含量
	$user_id = $_GET['user_id'];
	$device_id = $_GET['device_id'];
	
	$datetime = date ("Y-m-d H:i:s" , mktime(date('H')+8, date('i'), date('s'), date('m'), date('d'), date('Y'))) ; 
	
	//拿到圓餅圖的範例 : p3=3D圓餅圖(p=一般圓餅圖)/lc=折線圖 / chd=各項比例 / chs=圖片大小 / chl=各項標示名稱 / chco=顏色設定 / chdl=右邊標示名稱
	//下面有兩個範例
	//https://chart.googleapis.com/chart?cht=p3&chd=t:30,50&chs=500x240&chl=臭屁含量|一般氣體
	//https://chart.googleapis.com/chart?cht=p3&chd=t:20,30,50&chs=500x240&chl=20%|30%|50%&chco=00AAFF,000000,AA0044&chdl=A|B|C
	
	//折線圖範例
	//https://chart.googleapis.com/chart?cht=lc&chd=t:30,50,45,60,75,100,120&chs=600x300&chco=000000&chl=臭屁含量
	
	//通常的屁含量 (氮气590000ppm/氢气210000ppm/二氧化碳90000ppm/烷類70000ppm/氧气30000ppm/其他10000ppm)
	//ch LPG 拿到的是ppm值
	
	//拿到資料後，確認沒有問題，就先把圖片產出來吧~
	
	//這邊在設定URL
	$replace = str_ireplace("l",",",$P);
	$resultURL = "http://114.42.108.178/test/examples/?data=".$replace;
	
	$result = "哇！太臭屁了吧！";
	
	$sql = "insert into history (device_id, user_id,result,resultURL,date) values ('$device_id','$user_id','$result','$resultURL','$datetime')";
	if(mysqli_query($db,$sql))
	{
		$findID = "select * from history where date='$datetime'";
		$result = mysqli_fetch_object($db->query($findID));
		echo $result->history_id;
	}
	else
		echo 0;
	
?>