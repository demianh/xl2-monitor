CREATE TABLE `measures` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `timestamp` int(11) NOT NULL,
  `station` varchar(20) NOT NULL DEFAULT '',
  `value` float NOT NULL,
  PRIMARY KEY (`id`),
  KEY `station` (`station`,`timestamp`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;
