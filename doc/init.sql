DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `permission`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `enabled` varchar(1) NOT NULL DEFAULT '0' COMMENT '是否可用',
  `accountExpireAt` datetime NOT NULL COMMENT '账号到期时间',
  `passwordExpireAt` datetime NOT NULL COMMENT '用户密码到期时间',
  `hasLocked` varchar(1) NOT NULL COMMENT '账号是否锁定',
  `openId` varchar(64) DEFAULT NULL COMMENT '微信openId',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
`id` bigint(11) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NOT NULL,
PRIMARY KEY (`id`)
);
CREATE TABLE `user_role` (
`user_id` bigint(11) NOT NULL,
`role_id` bigint(11) NOT NULL
);
CREATE TABLE `role_permission` (
`role_id` bigint(11) NOT NULL,
`permission_id` bigint(11) NOT NULL
);
CREATE TABLE `permission` (
`id` bigint(11) NOT NULL AUTO_INCREMENT,
`url` varchar(255) NOT NULL,
`name` varchar(255) NOT NULL,
`description` varchar(255) NULL,
`pid` bigint(11) NOT NULL,
PRIMARY KEY (`id`)
);

INSERT INTO user(`id`, `username`, `password`, `enabled`, `account_expire_at`, `password_expire_at`, `has_locked`, `open_id`) VALUES (1, 'user', 'e10adc3949ba59abbe56e057f20f883e', '0', '2020-08-30 11:39:39', '2020-08-30 11:39:48', '0', '0');
INSERT INTO user(`id`, `username`, `password`, `enabled`, `account_expire_at`, `password_expire_at`, `has_locked`, `open_id`) VALUES (2, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '1', '2020-08-31 11:40:09', '2020-08-31 11:40:15', '0', '123');


INSERT INTO role (id, name) VALUES (1,'USER');
INSERT INTO role (id, name) VALUES (2,'ADMIN');
INSERT INTO permission (id, url, name, pid) VALUES (1,'/user/common','common',0);
INSERT INTO permission (id, url, name, pid) VALUES (2,'/user/admin','admin',0);
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 1);
INSERT INTO role_permission (role_id, permission_id) VALUES (2, 1);
INSERT INTO role_permission (role_id, permission_id) VALUES (2, 2);