<?php
	$xdata = array('測試一','測試二','測試三','測試四','測試五','測試六','測試七','測試八','測試九');
 $ydata = array(array(,,,,,,,,),array(,,,,,,,,));
 $color = array();
 $seriesName = array("七月","八月");
 $title = "測試數據";
 $Img = new Chart($title,$xdata,$ydata,$seriesName);
 $Img->paintLineChart();
?>