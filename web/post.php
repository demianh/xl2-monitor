<?php

require_once 'vendor/autoload.php';
require '_conf.php';

use Doctrine\DBAL\DriverManager;
$DB = DriverManager::getConnection($SQL_CREDENTIALS, new \Doctrine\DBAL\Configuration());

if(isset($_REQUEST)){
	$json = file_get_contents("php://input");
	$data = json_decode($json, true);

	if($data && isset($data['station']) && isset($data['value'])){
		$row = [
			'timestamp' => time(),
			'station' => $data['station'],
			'value' => $data['value']
		];
		$DB->insert('measures', $row);
		echo $data['value'];
		return;
	}
	die('invalid data.');
}