-- ============================================
-- 数据库名称：music_box
-- 字符集：utf8mb4
-- 使用方法：mysql -uroot -p < sql/music_box.sql
-- ============================================
CREATE DATABASE IF NOT EXISTS `music_box`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `music_box`;

-- ============================================
-- 1. 用户表 (user)
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `role` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 初始化测试账号（用户名：user，密码：123）
INSERT INTO `user` (`username`, `password`, `role`, `nickname`)
VALUES ('user', '123', 2, '用户');

-- ============================================
-- 2. 音乐表 (music)
-- ============================================
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '音乐ID',
  `name` VARCHAR(100) NOT NULL COMMENT '歌曲名称',
  `singer` VARCHAR(100) DEFAULT NULL COMMENT '歌手',
  `album` VARCHAR(100) DEFAULT NULL COMMENT '专辑',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
  `file_url` VARCHAR(255) NOT NULL COMMENT '音乐文件URL',
  `duration` INT(11) DEFAULT 0 COMMENT '时长（秒）',
  `play_count` INT(11) NOT NULL DEFAULT 0 COMMENT '播放次数',
  `is_hot` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否热门：0-否，1-是',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_singer` (`singer`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐表';

-- 插入测试数据
INSERT INTO `music` (`name`, `singer`, `album`, `cover_url`, `file_url`, `duration`, `is_hot`) VALUES
('夜曲', '周杰伦', '十一月的萧邦', '/covers/yequ.jpg', '/music/yequ.mp3', 230, 1),
('七里香', '周杰伦', '七里香', '/covers/qilixiang.jpg', '/music/qilixiang.mp3', 240, 1),
('海阔天空', 'Beyond', '乐与怒', '/covers/haikuotiankong.jpg', '/music/haikuotiankong.mp3', 280, 1),
('光年之外', '邓紫棋', '光年之外', '/covers/guangnianzhiwai.jpg', '/music/guangnianzhiwai.mp3', 220, 0),
('孤勇者', '陈奕迅', '孤勇者', '/covers/guyongzhe.jpg', '/music/guyongzhe.mp3', 260, 0);

-- ============================================
-- 3. 收藏表 (favorite)
-- ============================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `music_id` INT(11) NOT NULL COMMENT '音乐ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '收藏备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_music` (`user_id`, `music_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_music_id` (`music_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorite_music` FOREIGN KEY (`music_id`) REFERENCES `music` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 4. 评论表 (comment)
-- ============================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `music_id` INT(11) NOT NULL COMMENT '音乐ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` INT(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_music_id` (`music_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_music` FOREIGN KEY (`music_id`) REFERENCES `music` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 插入测试评论
INSERT INTO `comment` (`user_id`, `music_id`, `content`) VALUES
(1, 1, '经典中的经典！太好听了！'),
(1, 2, '满满的青春回忆~'),
(1, 3, 'Beyond永远的神！');

-- ============================================
-- 5. 热门推荐表 (hot_recommend)
-- ============================================
DROP TABLE IF EXISTS `hot_recommend`;
CREATE TABLE `hot_recommend` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `music_id` INT(11) NOT NULL COMMENT '音乐ID',
  `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序序号（越小越靠前）',
  `recommend_title` VARCHAR(100) DEFAULT NULL COMMENT '推荐标题（如：本周热歌）',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_music_id` (`music_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_hot_music` FOREIGN KEY (`music_id`) REFERENCES `music` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='热门推荐表';

-- 插入测试推荐数据
INSERT INTO `hot_recommend` (`music_id`, `sort_order`, `recommend_title`) VALUES
(1, 1, '本周热歌'),
(2, 2, '本周热歌'),
(3, 3, '经典永流传');

-- ============================================
-- 6. 操作日志表 (operation_log)
-- ============================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` INT(11) DEFAULT NULL COMMENT '操作用户ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
  `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
  `detail` TEXT DEFAULT NULL COMMENT '操作详情',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
