<?php

require 'vendor/autoload.php';
require '_conf.php';

use Doctrine\DBAL\DriverManager;

$DB = DriverManager::getConnection($SQL_CREDENTIALS, new \Doctrine\DBAL\Configuration());

$app = new Slim\App();

/**
 * Returns a list of all stations with the latest measurement in the last 12 hours
 */
$app->get('/stations', function ($request, $response, $args) use(&$DB) {

	$stations = $DB->fetchAll('
		SELECT t.timestamp, t.station, t.value FROM measures t JOIN (
			SELECT station, MAX(timestamp) maxts
			FROM measures
			GROUP BY station
		) r ON t.station = r.station AND t.timestamp = r.maxts
		WHERE t.timestamp > UNIX_TIMESTAMP() - 60 * 60 * 12
		GROUP BY t.timestamp, t.station
		ORDER BY t.station ASC
	');

	return $response->withJson($stations);
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
