<?php

require 'vendor/autoload.php';
require '_conf.php';

use Doctrine\DBAL\DriverManager;

$DB = DriverManager::getConnection($SQL_CREDENTIALS, new \Doctrine\DBAL\Configuration());

if (!isset($_GET['station'])){
	die();
}
$station = $_GET['station'];

$measures = $DB->fetchAll('
	SELECT timestamp, value FROM measures 
	WHERE station = ?
	GROUP BY timestamp
	ORDER BY timestamp ASC
', [$station]);

$data = [];
foreach ($measures as $measure) {
	$data[] =  '{ x: '.$measure['timestamp'].', y:'.$measure['value'].'}';
}

?>
<!doctype html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Chart</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/canvasjs/1.7.0/jquery.canvasjs.min.js"></script>
<script type="text/javascript">
  window.onload = function () {
    var chart = new CanvasJS.Chart("chartContainer",
    {
      zoomEnabled: true,

      title:{
       text: "dB Messung"
     },

     data: [
     {
      type: "area",
      xValueType: "dateTime",
      dataPoints: [
      	<?php echo implode(',', $data) ?>
      ]
    }
    ]
  });

    chart.render();
  }
  </script>
</head>
<body>

	<div id="chartContainer" style="height: 300px; width: 100%;"></div>

</body>
</html>

