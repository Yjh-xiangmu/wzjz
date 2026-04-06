/*
 Navicat Premium Data Transfer

 Source Server         : yjh
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : wzjz_db

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 06/04/2026 16:19:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for biz_demand
-- ----------------------------
DROP TABLE IF EXISTS `biz_demand`;
CREATE TABLE `biz_demand`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `recipient_id` bigint NOT NULL COMMENT '受助方用户ID(关联sys_user)',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所需物资名称/标题',
  `quantity` int NOT NULL COMMENT '所需数量',
  `matched_quantity` int NULL DEFAULT 0 COMMENT '已匹配数量',
  `urgency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '紧急程度: 一般, 紧急, 特急',
  `background` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '受益群体背景说明',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货地址',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待审核, 1求助中(审核通过), 2已完成, 3已下架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发布时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '受助方需求表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_demand
-- ----------------------------
INSERT INTO `biz_demand` VALUES (1, 3, '小米手机50部', 50, 34, '特急', '给山区孩子', '某某山区', '13900000000', 1, '2026-04-06 13:09:04', '2026-04-06 13:47:21');
INSERT INTO `biz_demand` VALUES (2, 3, '非洲之心', 1, 0, '特急', '玩', '大坝', '13900000000', 1, '2026-04-06 15:39:43', '2026-04-06 15:40:33');

-- ----------------------------
-- Table structure for biz_feedback
-- ----------------------------
DROP TABLE IF EXISTS `biz_feedback`;
CREATE TABLE `biz_feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联的订单ID',
  `donor_id` bigint NOT NULL COMMENT '捐助者ID',
  `recipient_id` bigint NOT NULL COMMENT '受助方ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '感谢信正文',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '现场受助照片(多图逗号分隔)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '受助方感恩回馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_feedback
-- ----------------------------
INSERT INTO `biz_feedback` VALUES (1, 4, 1, 3, '代替阿萨拉谢谢你。', '', '2026-04-06 14:16:50');
INSERT INTO `biz_feedback` VALUES (2, 3, 1, 3, '感谢老大', '/uploads/0095bbeb79964e89be821484271e9f28.png', '2026-04-06 14:27:38');

-- ----------------------------
-- Table structure for biz_material
-- ----------------------------
DROP TABLE IF EXISTS `biz_material`;
CREATE TABLE `biz_material`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `donor_id` bigint NOT NULL COMMENT '捐助者用户ID(关联sys_user表)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物资名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统分类(如衣物、书籍、食品、医疗用品等)',
  `quantity` int NOT NULL COMMENT '数量',
  `condition_degree` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '新旧程度(如全新、九成新等)',
  `validity_date` date NULL DEFAULT NULL COMMENT '有效期(针对食品、药品等，可为空)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '详细补充说明',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物资实拍图片路径(多张图片用逗号分隔)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待捐助(默认), 1已匹配待发货, 2物流运输中, 3已签收完成, 4审核驳回/下架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发布时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '捐助物资详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_material
-- ----------------------------
INSERT INTO `biz_material` VALUES (1, 1, '小米手机', '数码电器', 1, '全新', NULL, '资助给**山区。', ',,', 1, '2026-04-06 12:51:13', '2026-04-06 13:22:07');
INSERT INTO `biz_material` VALUES (2, 1, '小米', '数码电器', 1, '全新', NULL, '11111', ',', 3, '2026-04-06 12:56:00', '2026-04-06 14:18:21');
INSERT INTO `biz_material` VALUES (3, 1, '小米17', '数码电器', 2, '全新', NULL, '山区', '/uploads/46a1a31b5cec496ca2f3eefef2a65d06.jpg,/uploads/edf9f1f34cec4e9cb4f7c0e46bcc1694.jpg,/uploads/1782cd558c5549849236f8655badc7f8.jpg', 3, '2026-04-06 13:03:35', '2026-04-06 14:18:19');
INSERT INTO `biz_material` VALUES (4, 1, '小米手机', '数码电器', 30, '全新', NULL, '30部给山区', '/uploads/4814ba8e915c4c37aa3e44d6dfc2c12b.jpg,/uploads/1ad10378aded4b89a196a9862d889cba.jpg,/uploads/642864460ce04d39bed6bf939e053371.jpg', 3, '2026-04-06 13:29:24', '2026-04-06 14:13:44');

-- ----------------------------
-- Table structure for biz_message
-- ----------------------------
DROP TABLE IF EXISTS `biz_message`;
CREATE TABLE `biz_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NULL DEFAULT 0 COMMENT '0代表基于人的沟通，不再强绑定单个订单',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '留言内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '状态: 0未读, 1已读',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '沟通留言表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_message
-- ----------------------------
INSERT INTO `biz_message` VALUES (1, 4, 1, 3, '1', 0, '2026-04-06 14:00:08');
INSERT INTO `biz_message` VALUES (2, 0, 3, 1, '感谢呀', 0, '2026-04-06 14:06:45');

-- ----------------------------
-- Table structure for biz_order
-- ----------------------------
DROP TABLE IF EXISTS `biz_order`;
CREATE TABLE `biz_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `demand_id` bigint NOT NULL COMMENT '需求ID',
  `material_id` bigint NOT NULL COMMENT '物资ID',
  `donor_id` bigint NOT NULL COMMENT '捐助者ID',
  `recipient_id` bigint NOT NULL COMMENT '受助方ID',
  `logistics_status` tinyint NULL DEFAULT 0 COMMENT '物流状态: 0待发货, 1运输中, 2已签收',
  `logistics_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物流单号',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '匹配成功(订单生成)时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '捐助匹配订单与物流追踪表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_order
-- ----------------------------
INSERT INTO `biz_order` VALUES (1, 1, 2, 1, 3, 2, '1', '1', '2026-04-06 13:21:29', '2026-04-06 13:21:29');
INSERT INTO `biz_order` VALUES (2, 1, 1, 1, 3, 0, NULL, NULL, '2026-04-06 13:22:07', '2026-04-06 13:22:07');
INSERT INTO `biz_order` VALUES (3, 1, 3, 1, 3, 2, '1', '1', '2026-04-06 13:22:15', '2026-04-06 13:22:15');
INSERT INTO `biz_order` VALUES (4, 1, 4, 1, 3, 2, '顺丰', 'a1sa6s1d5asd16as51d6', '2026-04-06 13:29:44', '2026-04-06 13:29:44');

-- ----------------------------
-- Table structure for sys_category
-- ----------------------------
DROP TABLE IF EXISTS `sys_category`;
CREATE TABLE `sys_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称(如衣物,书籍等)',
  `sort` int NULL DEFAULT 0 COMMENT '显示排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物资分类字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_category
-- ----------------------------
INSERT INTO `sys_category` VALUES (1, '数码电器', 1);
INSERT INTO `sys_category` VALUES (2, '衣物服装', 2);
INSERT INTO `sys_category` VALUES (3, '图书文具', 3);
INSERT INTO `sys_category` VALUES (4, '食品饮料', 4);

-- ----------------------------
-- Table structure for sys_complaint
-- ----------------------------
DROP TABLE IF EXISTS `sys_complaint`;
CREATE TABLE `sys_complaint`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '提交人ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '投诉或建议内容',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待处理, 1已处理',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '管理员回复内容',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '意见投诉反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_complaint
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告正文',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '公告' COMMENT '类型: 公告, 表彰, 资讯',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告与表彰表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(加密保存)',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色: donor(捐助者), recipient(受助方), admin(管理员)',
  `org_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构/受助方名称',
  `org_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '机构简介',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0正常, 1禁用, 2待审核(受助方专属)',
  `points` int NULL DEFAULT 0 COMMENT '爱心积分',
  `qualifications` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资质证明图片路径(受助方注册必填)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '注册时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '15903852598', 'e10adc3949ba59abbe56e057f20f883e', 'donor', NULL, NULL, 0, 300, '', '2026-04-06 11:34:46', '2026-04-06 11:34:46');
INSERT INTO `sys_user` VALUES (2, '13000000000', 'e10adc3949ba59abbe56e057f20f883e', 'donor', NULL, NULL, 0, 0, '', '2026-04-06 11:57:16', '2026-04-06 11:57:16');
INSERT INTO `sys_user` VALUES (3, '13900000000', 'e10adc3949ba59abbe56e057f20f883e', 'recipient', '大坝', '阿萨拉卫队', 0, 0, '/uploads/edf89527cc194408804e1e4fc3ffc35d.png', '2026-04-06 11:57:37', '2026-04-06 15:39:15');
INSERT INTO `sys_user` VALUES (4, '19999999999', 'e10adc3949ba59abbe56e057f20f883e', 'admin', NULL, NULL, 0, 0, NULL, '2026-04-06 12:01:23', '2026-04-06 12:01:23');

SET FOREIGN_KEY_CHECKS = 1;
