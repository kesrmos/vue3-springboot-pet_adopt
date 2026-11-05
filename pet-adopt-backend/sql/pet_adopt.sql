-- ============================================================
-- 宠物领养系统 数据库脚本（MySQL 8.0+ / 8.4 / 9.x）
-- 用法：
--   mysql -u root -p --default-character-set=utf8mb4 < sql/pet_adopt.sql
-- 注意：
--   1. 执行会 DROP 并重建 pet_adopt，请先确认无重要数据；
--   2. 不建物理外键，关联关系由业务代码保证；
--   3. 密码明文存储（学习项目，不做加密）。
-- ============================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `pet_adopt`;
CREATE DATABASE `pet_adopt` DEFAULT CHARACTER SET utf8mb4;
USE `pet_adopt`;

DROP TABLE IF EXISTS `adopt_apply`;
DROP TABLE IF EXISTS `pet`;
DROP TABLE IF EXISTS `user`;

-- ---------- 1. 用户表 ----------
CREATE TABLE `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`    VARCHAR(50)  NOT NULL                COMMENT '登录账号',
  `password`    VARCHAR(100) NOT NULL                COMMENT '密码(明文)',
  `name`        VARCHAR(50)  DEFAULT NULL            COMMENT '姓名',
  `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
  `address`     VARCHAR(255) DEFAULT NULL            COMMENT '地址',
  `role`        TINYINT      NOT NULL DEFAULT 0      COMMENT '角色:0普通用户,1管理员',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  CONSTRAINT `chk_user_role` CHECK (`role` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------- 2. 宠物表 ----------
CREATE TABLE `pet` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(50)   NOT NULL                COMMENT '宠物名称',
  `type`        VARCHAR(20)   NOT NULL                COMMENT '类型:猫/狗/兔/鸟...',
  `gender`      VARCHAR(10)   DEFAULT NULL            COMMENT '性别:公/母',
  `age`         INT           DEFAULT NULL            COMMENT '年龄(月)',
  `photo`       VARCHAR(500)  DEFAULT NULL            COMMENT '照片地址',
  `description` VARCHAR(1000) DEFAULT NULL            COMMENT '描述',
  `status`      TINYINT       NOT NULL DEFAULT 0      COMMENT '状态:0待领养,1已领养',
  `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `chk_pet_status` CHECK (`status` IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物表';

-- ---------- 3. 领养申请表 ----------
CREATE TABLE `adopt_apply` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`      BIGINT       NOT NULL                COMMENT '申请用户id',
  `pet_id`       BIGINT       NOT NULL                COMMENT '宠物id',
  `live_env`     VARCHAR(255) NOT NULL                COMMENT '居住环境',
  `exp`          VARCHAR(500) DEFAULT NULL            COMMENT '养宠经验',
  `remark`       VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `audit_status` TINYINT      NOT NULL DEFAULT 0      COMMENT '审核状态:0待审核,1通过,2驳回',
  `audit_remark` VARCHAR(500) DEFAULT NULL            COMMENT '审核意见',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `chk_apply_audit_status` CHECK (`audit_status` IN (0, 1, 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

-- ---------- 初始化数据 ----------
-- 管理员：admin / 123456
-- 普通用户：zhangsan / 123456、lisi / 123456
INSERT INTO `user` (`id`,`username`,`password`,`name`,`phone`,`address`,`role`,`create_time`) VALUES
(1, 'admin',    '123456', '系统管理员', '13800000000', '北京市朝阳区宠物之家',      1, '2026-01-01 09:00:00'),
(2, 'zhangsan', '123456', '张三',       '13811112222', '北京市海淀区中关村1号',     0, '2026-01-01 09:05:00'),
(3, 'lisi',     '123456', '李四',       '13833334444', '上海市浦东新区世纪大道2号', 0, '2026-01-01 09:10:00');

INSERT INTO `pet` (`id`,`name`,`type`,`gender`,`age`,`photo`,`description`,`status`,`create_time`) VALUES
(1, '豆豆', '狗', '公',  8, 'https://picsum.photos/id/237/600/400',  '金毛幼犬，活泼亲人，已完成第一针疫苗。', 0, '2026-01-02 10:00:00'),
(2, '咪咪', '猫', '母', 12, 'https://picsum.photos/id/40/600/400',   '狸花猫，性格温顺，会用猫砂盆。',         0, '2026-01-02 10:05:00'),
(3, '阿黄', '狗', '公', 24, 'https://picsum.photos/id/1074/600/400', '中华田园犬，忠诚护家，适合独栋小院。',   0, '2026-01-02 10:10:00'),
(4, '雪球', '兔', '母',  6, 'https://picsum.photos/id/582/600/400',  '垂耳兔，爱吃提摩西草，需笼养。',         0, '2026-01-02 10:15:00'),
(5, '大黄', '猫', '公', 18, 'https://picsum.photos/id/1025/600/400', '橘猫，吃货一枚，已绝育。',               1, '2026-01-02 10:20:00'),
(6, '皮皮', '狗', '母', 10, 'https://picsum.photos/id/659/600/400',  '柯基，短腿活泼，已训练定点如厕。',       0, '2026-01-02 10:25:00');

INSERT INTO `adopt_apply` (`id`,`user_id`,`pet_id`,`live_env`,`exp`,`remark`,`audit_status`,`audit_remark`,`create_time`) VALUES
(1, 2, 5, '自有住房，带阳台', '养过2年猫', '希望领养橘猫', 1, '条件良好，同意领养', '2026-01-03 11:00:00'),
(2, 3, 1, '合租公寓',        '无',        '很喜欢金毛',   0, NULL,                '2026-01-03 11:10:00');

ALTER TABLE `user`        AUTO_INCREMENT = 4;
ALTER TABLE `pet`         AUTO_INCREMENT = 7;
ALTER TABLE `adopt_apply` AUTO_INCREMENT = 3;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------- 执行后验证（可整段再跑一次） ----------
SHOW TABLES;
SELECT COUNT(*) AS user_count  FROM `user`;         -- 期望 3
SELECT COUNT(*) AS pet_count   FROM `pet`;          -- 期望 6
SELECT COUNT(*) AS apply_count FROM `adopt_apply`;  -- 期望 2
SELECT id,name,type,status FROM `pet` ORDER BY id;
SELECT id,username,role FROM `user` ORDER BY id;
