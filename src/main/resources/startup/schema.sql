-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
CREATE TABLE IF NOT EXISTS `persistent_logins`  (
  `username` varchar(64)  NOT NULL,
  `series` varchar(64)  NOT NULL,
  `token` varchar(64)  NOT NULL,
  `last_used` timestamp(0) NOT NULL DEFAULT current_timestamp(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`series`)
);