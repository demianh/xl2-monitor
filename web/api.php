<?php

require 'vendor/autoload.php';
require '_conf.php';

use Doctrine\DBAL\DriverManager;

$DB = DriverManager::getConnection($SQL_CREDENTIALS, new \Doctrine\DBAL\Configuration());

$app = new Slim\App();

$app->get('/stations', function ($request, $response, $args) use(&$DB) {

	$stations = $DB->fetchAll('
		SELECT t.timestamp, t.station, t.value FROM measures t JOIN (
			SELECT station, MAX(timestamp) maxts
			FROM measures
			GROUP BY station
		) r ON t.station = r.station AND t.timestamp = r.maxts
		ORDER BY t.timestamp desc
	');
	//echo json_encode($stations);
	return $response->withJson($stations);
});

$app->run();
