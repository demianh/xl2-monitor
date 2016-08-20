<?php

require 'vendor/autoload.php';
require '_conf.php';

use Doctrine\DBAL\DriverManager;

$DB = DriverManager::getConnection($SQL_CREDENTIALS, new \Doctrine\DBAL\Configuration());

$app = new Slim\App();

/**
 * Returns a list of all stations with the latest measurement in the last hour
 */
$app->get('/stations', function ($request, $response, $args) use(&$DB) {

	$stations = $DB->fetchAll('
		SELECT t.timestamp, t.station, t.value FROM measures t JOIN (
			SELECT station, MAX(timestamp) maxts
			FROM measures
			GROUP BY station
		) r ON t.station = r.station AND t.timestamp = r.maxts
		WHERE t.timestamp > UNIX_TIMESTAMP() - 60 * 60 * 1
		GROUP BY t.timestamp, t.station
		ORDER BY t.station ASC
	');

	return $response->withJson($stations);
});

/**
 * Returns a list of all measurements for a station
 */
$app->get('/export/{station}', function ($request, $response, $args) use(&$DB) {

	$station = $request->getAttribute('station');
	$measures = $DB->fetchAll('
		SELECT timestamp, value FROM measures 
		WHERE station = ?
		GROUP BY timestamp
		ORDER BY timestamp ASC
	', [$station]);

	$csv = [];
	foreach ($measures as $measure) {
		$csv[] = date("Y-m-d H:i:s", $measure['timestamp']) . "," . $measure['value'];
	}

	return $response->getBody()->write(implode("<br>\n", $csv));
});

/**
 * Collects a measurement
 * Format: {"station":"Stationname", "value":84.0}
 */
$app->post('/', function ($request, $response, $args) use(&$DB) {
	$json = file_get_contents("php://input");
	$data = json_decode(urldecode($json), true);

	if($data && isset($data['station']) && isset($data['value'])){
		$row = [
			'timestamp' => time(),
			'station' => $data['station'],
			'value' => $data['value']
		];
		$DB->insert('measures', $row);
		$response->getBody()->write($data['value']);
		return $response;
	}
	die('invalid data.');
});

$app->run();
