/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80405 (8.4.5)
 Source Host           : localhost:3306
 Source Schema         : chat

 Target Server Type    : MySQL
 Target Server Version : 80405 (8.4.5)
 File Encoding         : 65001

 Date: 26/07/2025 18:49:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat
-- ----------------------------
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat`  (
  `chat_room_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '聊天室uuid',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息发送人',
  `chat` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '聊天内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发送时间',
  `status` int NOT NULL COMMENT '1为正常0为删除',
  PRIMARY KEY (`chat_room_id`) USING BTREE,
  INDEX `username`(`username` ASC) USING BTREE,
  CONSTRAINT `chat_room_id` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_room` (`uuid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `username` FOREIGN KEY (`username`) REFERENCES `user_information` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat
-- ----------------------------

-- ----------------------------
-- Table structure for chat_room
-- ----------------------------
DROP TABLE IF EXISTS `chat_room`;
CREATE TABLE `chat_room`  (
  `uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间的唯一id',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `status` int NOT NULL COMMENT '1为正常 2为关闭',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`uuid`) USING BTREE,
  INDEX `create_user`(`create_user` ASC) USING BTREE,
  CONSTRAINT `create_user` FOREIGN KEY (`create_user`) REFERENCES `user_information` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_room
-- ----------------------------
INSERT INTO `chat_room` VALUES ('2457b907-66e7-48e2-b5e9-0d3a200390cd', 'lingview', 1, '2025-07-26 18:09:18');
INSERT INTO `chat_room` VALUES ('6f4009ea-6935-4f83-8f7f-cb8249f7d20d', 'admin', 2, '2025-07-26 17:26:42');
INSERT INTO `chat_room` VALUES ('8a0dec09-1084-443f-bdcb-7e212796a973', 'admin', 1, '2025-07-26 17:15:55');
INSERT INTO `chat_room` VALUES ('a0d2b1fa-ded6-47a7-a0a6-69eb06efb344', 'admin', 2, '2025-07-26 17:27:54');
INSERT INTO `chat_room` VALUES ('b300b008-7d6f-44e6-98b0-cc66752d777a', 'admin', 2, '2025-07-26 17:31:47');
INSERT INTO `chat_room` VALUES ('b642df2f-c73b-41aa-afe1-8e1323713e79', 'admin', 2, '2025-07-26 17:26:50');
INSERT INTO `chat_room` VALUES ('c1737622-f394-4808-8094-ba18ce29d3e9', 'admin', 1, '2025-07-26 17:26:32');
INSERT INTO `chat_room` VALUES ('f9a35aa2-e63d-41aa-ab81-597c17966f5a', 'admin', 2, '2025-07-26 17:30:18');

-- ----------------------------
-- Table structure for user_information
-- ----------------------------
DROP TABLE IF EXISTS `user_information`;
CREATE TABLE `user_information`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id，自增',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名，不能重复',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码，使用md5加密',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int NOT NULL COMMENT '用户状态，普通用户：1，管理员：0，封禁用户：2',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_information
-- ----------------------------
INSERT INTO `user_information` VALUES (1, 'admin', 'official@lingview.xyz', 'ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413', '2025-07-26 16:30:22', 0);
INSERT INTO `user_information` VALUES (2, 'lingview', 'official@lingview.xyz', 'aa7527886047e0b1e1ad137776741c770513d937d1df141f4c1103b8954ad5ec66b556228dc8cf8c24ac62f62321f54f22b18819ae64a02b7ffe3fab0fd9cdc3', '2025-07-26 16:33:53', 1);

SET FOREIGN_KEY_CHECKS = 1;
