<?php

$SQL_CREDENTIALS = array(
	'dbname' => 'xl2monitor',
	'user' => 'root',
	'password' => 'local',
	'host' => '127.0.0.1',
	'driver' => 'pdo_mysql',
	'charset' => 'utf8',
	'driverOptions' => array(
		1002=>'SET NAMES utf8'
	)
);

define('SESSION_LIFETIME', 60 * 60 * 24 * 30); // 30 days

date_default_timezone_set('Europe/Zurich');
