-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: fifteen_hundun
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `memo`
--

DROP TABLE IF EXISTS `memo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `memo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '备忘ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `title` varchar(100) NOT NULL COMMENT '备忘标题',
  `content` text COMMENT '小内容直接存储',
  `content_path` varchar(255) DEFAULT NULL COMMENT '内容文件路径（大内容时使用）',
  `main_type` tinyint NOT NULL COMMENT '主类型：1-必须铭记，2-日常规划',
  `sub_type` tinyint DEFAULT NULL COMMENT '子类型：1-人生警示，2-重要数据（仅main_type=1时有效）',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-正常，0-删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='备忘表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memo`
--

LOCK TABLES `memo` WRITE;
/*!40000 ALTER TABLE `memo` DISABLE KEYS */;
/*!40000 ALTER TABLE `memo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operation_log`
--

DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operate_user_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operate_user_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `operate_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型：新增/修改/删除/查询',
  `module_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作IP地址',
  `operate_function_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作的方法名',
  `operate_at` datetime DEFAULT NULL COMMENT '操作时间',
  `operate_time` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作时长（建议记录为秒/毫秒，如"500ms"）',
  `operate_status` tinyint DEFAULT NULL COMMENT '操作状态：1成功 0失败',
  `executed_sql` text COLLATE utf8mb4_unicode_ci COMMENT '执行的SQL语句',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_operate_user_id` (`operate_user_id`) USING BTREE,
  KEY `idx_operate_at` (`operate_at`) USING BTREE,
  KEY `idx_module_name` (`module_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation_log`
--

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
INSERT INTO `operation_log` VALUES (1,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1',NULL,'2025-12-18 20:43:07','565ms',1,NULL),(2,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1',NULL,'2025-12-18 20:44:24','536ms',1,NULL),(3,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:48:49','512ms',1,NULL),(4,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:48:56','15ms',1,NULL),(5,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:49:03','8ms',1,NULL),(6,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:49:08','40ms',1,NULL),(7,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:49:13','7ms',1,NULL),(8,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:52:48','520ms',1,NULL),(9,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:53:55','539ms',1,NULL),(10,11,'admin','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-18 20:54:08','4ms',0,NULL),(11,11,'admin','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-25 09:37:42','284ms',1,NULL),(12,11,'admin','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-25 10:10:56','38ms',1,NULL),(13,11,'admin','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-25 10:11:06','36ms',1,NULL),(14,11,'admin','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-25 10:17:39','255ms',1,NULL),(15,11,'admin','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-25 10:17:48','28ms',1,NULL),(16,11,'admin','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-25 10:18:23','40ms',1,NULL),(17,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 18:42:21','68ms',1,NULL),(18,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getUsers','2025-12-26 19:09:51','376ms',1,NULL),(19,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:10:18','49ms',1,NULL),(20,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:11:47','14ms',1,NULL),(21,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:12:17','380ms',1,NULL),(22,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:12:20','18ms',1,NULL),(23,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:13:01','382ms',1,NULL),(24,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:14:28','387ms',1,NULL),(25,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:17:38','353ms',1,NULL),(26,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:18:55','378ms',1,NULL),(27,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:19:13','10ms',1,NULL),(28,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:19:22','20ms',1,NULL),(29,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:19:30','16ms',1,NULL),(30,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:20:20','45ms',1,NULL),(31,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:20:27','14ms',1,NULL),(32,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:21:07','19ms',1,NULL),(33,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:21:25','11ms',1,NULL),(34,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:21:34','10ms',1,NULL),(35,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:21:48','9ms',1,NULL),(36,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:21:57','38ms',1,NULL),(37,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:22:06','3ms',1,NULL),(38,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:23:38','10ms',1,NULL),(39,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:23:45','5ms',1,NULL),(40,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:23:51','5ms',1,NULL),(41,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:25:49','353ms',1,NULL),(42,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:25:57','14ms',1,NULL),(43,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:26:32','15ms',1,NULL),(44,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:28:02','17ms',1,NULL),(45,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:30:26','433ms',1,NULL),(46,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:30:29','21ms',1,NULL),(47,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:31:14','32ms',1,NULL),(48,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:31:25','15ms',1,NULL),(49,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:31:38','16ms',1,NULL),(50,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:31:46','5ms',1,NULL),(51,1,'wuqi','SELECT','用户模块','0:0:0:0:0:0:0:1','getLogs','2025-12-26 19:31:55','11ms',1,NULL),(54,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:09:11','76ms',1,NULL),(55,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:09:12','17ms',1,NULL),(56,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:14:18','76ms',1,NULL),(57,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:14:19','8ms',1,NULL),(58,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:17:47','88ms',1,NULL),(59,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:17:48','16ms',1,NULL),(60,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:19:07','71ms',1,NULL),(61,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:19:08','7ms',1,NULL),(62,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:21:34','5ms',1,NULL),(63,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:21:35','4ms',1,NULL),(64,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:24:39','77ms',1,NULL),(65,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:24:40','7ms',1,NULL),(66,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:24:45','6ms',1,NULL),(67,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:24:45','3ms',1,NULL),(68,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:26:16','4ms',1,NULL),(69,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:26:17','3ms',1,NULL),(70,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:26:35','74ms',1,NULL),(71,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:26:36','6ms',1,NULL),(72,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:28:09','78ms',1,NULL),(73,NULL,NULL,'UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:28:09','9ms',1,NULL),(74,0,'login','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:31:43','82ms',1,NULL),(75,0,'login','UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:31:44','7ms',1,NULL),(76,0,'login','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:47:01','85ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'wuqi\''),(77,0,'login','UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:47:02','6ms',1,'UPDATE users SET email = \'1441208568@qq.com\', password_hash = \'$2a$12$k3NnkirVegVLuWhQxvZD0Oa4RCc47oDKQmv4gC3.U.7kUUsYhAkfW\', phone = \'17344765283\', last_login_at = \'2025-12-29T11:47:02.081103800\', status = 3, updated_at = \'2025-12-29T11:47:02.083615100\' WHERE username = \'wuqi\''),(79,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:48:38','2ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'lalala\''),(80,1,'wuqi','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-29 11:48:38','6ms',1,'delete from users where username = \'lalala\''),(81,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:49:07','4ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'lalala\''),(82,1,'wuqi','INSERT','用户模块','0:0:0:0:0:0:0:1','insertUser','2025-12-29 11:49:07','5ms',1,'INSERT INTO users ( username, email, password_hash, avatar_url, phone, created_at, updated_at ) VALUES ( \'lalala\', \'1441208568@qq.com\', \'$2a$12$PV77VeNeYVEotNWbIJZEJu3mt71V9VCqSL1ga0uh.fC3WDWDbD5s6\', \'\', \'17344765283\', \'2025-12-29T11:49:07.388906200\', \'2025-12-29T11:49:07.388906200\' )'),(83,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:49:35','3ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'lalala\''),(84,1,'wuqi','DELETE','用户模块','0:0:0:0:0:0:0:1','deleteUserByUserName','2025-12-29 11:49:36','2ms',1,'delete from users where username = \'lalala\''),(85,1,'wuqi','SELECT','管理员模块','0:0:0:0:0:0:0:1','getLogs','2025-12-29 11:50:01','129ms',1,'select id, operate_user_id as operateUserId, operate_user_name as operateUserName, operate_type as operateType, module_name as moduleName, operate_function_name as operateFunctionName, ip_address as ipAddress, operate_at as operateAt, operate_time as operateTime, operate_status as operateStatus from operation_log WHERE operate_status = \'0\' AND module_name like concat(\'%\', \'用户模块\', \'%\') AND operate_at >= \'2020-12-06T00:00\' AND operate_at <= \'2030-12-10T00:00\' order by operate_at desc LIMIT 3'),(87,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:50:40','2ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'lalala\''),(88,1,'wuqi','INSERT','用户模块','0:0:0:0:0:0:0:1','insertUser','2025-12-29 11:50:41','3ms',1,'INSERT INTO users ( username, email, password_hash, avatar_url, phone, created_at, updated_at ) VALUES ( \'lalala\', \'1441208568@qq.com\', \'$2a$12$dYx3OwOmIF4Tg2LqDwqb3uo.8vGSh5.3Rj1fKb43eitB.iBiyC616\', \'\', \'17344765283\', \'2025-12-29T11:50:40.693403200\', \'2025-12-29T11:50:40.693403200\' )'),(89,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:50:49','3ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'lalala\''),(90,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:50:49','4ms',1,'UPDATE users SET email = \'1441208568@qq.com\', password_hash = \'$2a$12$dYx3OwOmIF4Tg2LqDwqb3uo.8vGSh5.3Rj1fKb43eitB.iBiyC616\', phone = \'17344765283\', status = 0, updated_at = \'2025-12-29T11:50:49.457402300\' WHERE username = \'lalala\''),(91,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:51:13','3ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'lalala\''),(92,1,'wuqi','UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:51:13','5ms',1,'UPDATE users SET email = \'1441208568@qq.com\', password_hash = \'$2a$12$dYx3OwOmIF4Tg2LqDwqb3uo.8vGSh5.3Rj1fKb43eitB.iBiyC616\', phone = \'17344765283\', status = 1, updated_at = \'2025-12-29T11:51:13.420910500\' WHERE username = \'lalala\''),(93,1,'wuqi','INSERT','用户模块','0:0:0:0:0:0:0:1','insertUser','2025-12-29 11:51:57','20ms',1,'INSERT INTO users ( username, password_hash, status, created_at, updated_at, invite_code ) VALUES ( \'管理员待注册52711H06xO\', \'$2a$12$JU4dVHCO8TDMexlbL60YfOwXU1lW0hOZ2W9cHneUw36d/wjVoPJs.\', 0, \'2025-12-29T11:51:56.551637700\', \'2025-12-29T11:51:56.551637700\', \'52711H06xO\' )'),(94,0,'login','UPDATE','用户模块','0:0:0:0:0:0:0:1','selectByUserName','2025-12-29 11:56:27','72ms',1,'SELECT id, username, email, password_hash AS passwordHash, nickname, avatar_url AS avatarUrl, phone, status, created_at AS createdAt, updated_at AS updatedAt, last_login_at AS lastLoginAt FROM users WHERE username = \'wuqi\''),(95,0,'login','UPDATE','用户模块','0:0:0:0:0:0:0:1','updateUserByUserName','2025-12-29 11:56:28','7ms',1,'UPDATE users SET email = \'1441208568@qq.com\', password_hash = \'$2a$12$k3NnkirVegVLuWhQxvZD0Oa4RCc47oDKQmv4gC3.U.7kUUsYhAkfW\', phone = \'17344765283\', last_login_at = \'2025-12-29T11:56:28.005764800\', status = 3, updated_at = \'2025-12-29T11:56:28.007765700\' WHERE username = \'wuqi\'');
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `status` tinyint DEFAULT '1' COMMENT '1-正常 0-禁用',
  `invite_code` varchar(32) DEFAULT NULL COMMENT '管理员邀请码（唯一，普通用户为NULL）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `last_login_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `uk_invite_code` (`invite_code`) COMMENT '邀请码唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'wuqi','1441208568@qq.com','$2a$12$k3NnkirVegVLuWhQxvZD0Oa4RCc47oDKQmv4gC3.U.7kUUsYhAkfW',NULL,'','17344765283',3,NULL,'2025-12-11 18:20:09','2025-12-29 11:56:28','2025-12-29 11:56:28'),(11,'admin','1441208568@qq.com','$2a$12$1dWMlG/DBiFy6cIjf9nYh.X81zAV58qggvFHxSC7vT50cAAB2h0ty',NULL,'','17344765283',1,NULL,'2025-12-05 10:56:16','2025-12-25 09:24:25','2025-12-25 09:24:25'),(12,'hacker','1441208568@qq.com','$2a$12$THlwhpJQQTwjmQmU.J3T/eMCLCT5bWOgK6uLoRB2K7M2wcZV08ec6',NULL,'','17344765283',0,NULL,'2025-12-06 15:39:54','2025-12-06 15:42:44','2025-12-06 15:41:58'),(31,'lalala','1441208568@qq.com','$2a$12$dYx3OwOmIF4Tg2LqDwqb3uo.8vGSh5.3Rj1fKb43eitB.iBiyC616',NULL,'','17344765283',1,NULL,'2025-12-29 11:50:41','2025-12-29 11:51:13',NULL),(32,'管理员待注册52711H06xO',NULL,'$2a$12$JU4dVHCO8TDMexlbL60YfOwXU1lW0hOZ2W9cHneUw36d/wjVoPJs.',NULL,NULL,NULL,0,'52711H06xO','2025-12-29 11:51:57','2025-12-29 11:51:57',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-30 14:11:34
