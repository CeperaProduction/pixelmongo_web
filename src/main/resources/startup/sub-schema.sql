-- ----------------------------
-- Table structure for banlist
-- ----------------------------
CREATE TABLE IF NOT EXISTS `banlist`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(31) NOT NULL,
  `reason` varchar(255) NOT NULL,
  `admin` varchar(31) NOT NULL,
  `time` int(11) NOT NULL,
  `temptime` int(11) NOT NULL DEFAULT 0,
  `type` int(11) NOT NULL DEFAULT 0,
  `ip` varchar(16) NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- Table structure for logger
-- ----------------------------
CREATE TABLE IF NOT EXISTS `logger`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player` varchar(31) NOT NULL,
  `type` varchar(31) NOT NULL,
  `data` longtext NULL,
  `time` int(11) NULL DEFAULT NULL,
  `world` varchar(63) NOT NULL,
  `x` int(31) NULL DEFAULT NULL,
  `y` int(31) NULL DEFAULT NULL,
  `z` int(31) NULL DEFAULT NULL,
  `server` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);