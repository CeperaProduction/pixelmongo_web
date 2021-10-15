INSERT INTO `user_groups` VALUES (1, 'Администраторы', 100) ON DUPLICATE KEY UPDATE `name`=`name`;
INSERT INTO `user_groups` VALUES (2, 'Пользователи', 1) ON DUPLICATE KEY UPDATE `name`=`name`;

INSERT INTO `permission_list` VALUES (1, 'admin.panel.access') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (2, 'admin.panel.users') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (3, 'admin.panel.users.edit') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (4, 'admin.panel.groups') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (5, 'admin.panel.groups.edit') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (6, 'admin.panel.rules') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (7, 'admin.panel.logs') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (8, 'admin.panel.donate') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (9, 'admin.panel.monitoring') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (10, 'admin.panel.playerlogs') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (11, 'admin.panel.donate.content') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (12, 'admin.panel.donate.servers') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (13, 'admin.panel.donate.discount') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (14, 'admin.panel.donate.balances') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (15, 'admin.panel.donate.give') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (16, 'admin.panel.donate.logs') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (17, 'banlist.proof.upload') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (18, 'banlist.proof.delete') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (19, 'banlist.proof.all') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (20, 'admin.panel.staff') ON DUPLICATE KEY UPDATE `value`=`value`;
INSERT INTO `permission_list` VALUES (21, 'admin.panel.billing') ON DUPLICATE KEY UPDATE `value`=`value`;

INSERT INTO `users` (`name`, `email`, `password`, `group_id`, `reg_date`, `reg_ip`)
    SELECT * FROM (VALUES ('Admin', 'admin@pixelmongo.ru', 'c3284d0f94606de1fd2af172aba15bf3', 1, '2021-09-01 00:00:00.000000', '127.0.0.1')) AS v
    WHERE NOT EXISTS (SELECT * FROM `users`);

