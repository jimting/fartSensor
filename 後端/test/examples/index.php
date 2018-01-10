<?php 
	require_once __DIR__ . "/../src/Chartkick.php"; 
?>

<html>
    <head>
        <title>Chartkick</title>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/chartkick/2.2.1/chartkick.min.js"></script>
        <script src="https://www.gstatic.com/charts/loader.js"></script>
    </head>
    <body>
        <?php
			$dataArray = $_GET['data'];
			$String = preg_split("/,/", $dataArray);
			echo JonahGeorge\Chartkick::lineChart([
            "1" => $String[0],
            "2" => $String[1],
			"3" => $String[2],
			"4" => $String[3],
			"5" => $String[4]
        ]); 
		?>
    </body>
</html>
