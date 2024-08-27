-- MySQL dump 10.13  Distrib 9.0.1, for macos14.4 (arm64)
--
-- Host: localhost    Database: appworks91
-- ------------------------------------------------------
-- Server version	9.0.1

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
-- Table structure for table `campaigns`
--

DROP TABLE IF EXISTS `campaigns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaigns` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `terminate_at` timestamp NULL DEFAULT NULL,
  `discount_rate` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaigns`
--

LOCK TABLES `campaigns` WRITE;
/*!40000 ALTER TABLE `campaigns` DISABLE KEYS */;
/*!40000 ALTER TABLE `campaigns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logistics`
--

DROP TABLE IF EXISTS `logistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logistics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `order_no` varchar(20) DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL,
  `shipping` varchar(32) DEFAULT NULL,
  `allpaylogistic_id` varchar(20) DEFAULT NULL,
  `booking_note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `logistics_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logistics`
--

LOCK TABLES `logistics` WRITE;
/*!40000 ALTER TABLE `logistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `logistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_details`
--

DROP TABLE IF EXISTS `order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quantity` int DEFAULT NULL,
  `order_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `campaign_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  KEY `campaign_id` (`campaign_id`),
  CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`campaign_id`) REFERENCES `campaigns` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_details`
--

LOCK TABLES `order_details` WRITE;
/*!40000 ALTER TABLE `order_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line_user_id` varchar(33) NOT NULL,
  `order_status` int DEFAULT NULL,
  `total` int DEFAULT NULL,
  `create_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modify_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `method` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `price` int NOT NULL,
  `stock` int DEFAULT NULL,
  `category` varchar(32) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,100,100,'女裝','綠色輕薄長裙','078acc48-7e46-41b3-8612-c5704025eb40.jpg'),(2,200,100,'女裝','深色碎花洋裝','091710f7-0dbf-4cc1-8646-4adccd202198.jpg'),(3,300,100,'飾品','花紋手帕','17320fec-8eac-4517-b916-b4a152166271.jpg'),(4,400,100,'女裝','米色休閒短裙','1b465c1b-4442-440c-8ee4-6f65f57cec7e.jpg'),(5,500,100,'女裝','黃色無袖長裙','21410eae-16e7-4d9b-9890-e17d71f9cc94.jpg'),(6,600,100,'女裝','水色百褶長裙','2670959e-39e6-4fc1-9fd7-08e0bc4d8d2c.jpg'),(7,700,100,'女裝','黃色露肩長裙','2744ed86-60b4-4267-ae3e-cfc19a9b4d17.jpg'),(8,800,100,'女裝','白色碎花長裙','2a234829-b886-4ef2-85fa-52ed245e27ff.jpg'),(9,900,100,'女裝','綠色輕薄長裙','2e585135-5b90-44ac-a77d-60704bd7b19d.jpg'),(10,1000,100,'女裝','淺綠百褶長裙','36097dfa-dea8-4732-8080-20c2809f05e6.jpg'),(11,1100,100,'女裝','粉色花邊洋裝','3b6c39d8-14e3-43b3-a990-53aabe2fa5c4.jpg'),(12,1200,100,'女裝','橘色露肩洋裝','40bf7b33-d1c7-4b99-ab70-a32b8f1d01a0.jpg'),(13,1300,100,'女裝','淺綠露肩長裙','5fb18a9e-4579-4a84-8869-36e6e41495a0.jpg'),(14,1400,100,'女裝','白色碎花長裙','68321cac-1eb9-4975-871f-b8016642e70c.jpg'),(15,1500,100,'女裝','純白無袖洋裝','6d3db0b0-76cf-4af3-94fd-1b34d76e525f.jpg'),(16,1600,100,'女裝','粉色花邊洋裝','6f4144e5-f211-4f9f-b76f-f96fdc3f40b5.jpg'),(17,1700,100,'女裝','黑色露背長裙','70d7b61a-0578-44db-9c2f-63da1942303a.jpg'),(18,1800,100,'女裝','淺綠側開長裙','71a6c5e0-def5-4374-966e-aad78448ad73.jpg'),(19,1900,100,'女裝','黃色休閒長裙','7541acca-a927-4316-94c9-9649595b23f1.jpg'),(20,2000,100,'女裝','黑色神秘洋裝','81426207-974a-4d19-9892-38378fa1e650.jpg'),(21,2100,100,'女裝','黑色碎花短裙','9d1ab4db-8b5c-4d75-b335-075367f5ae74.jpg'),(22,2200,100,'女裝','米色休閒短裙','b04e3bdd-a86f-4837-a236-b4c60aa0d091.jpg'),(23,2300,100,'男裝','黑色緊身長褲','b0deb9c9-e515-4c10-9893-1f5c0052f868.jpg'),(24,2400,100,'女裝','粉色碎花洋裝','ba3cb7dc-7298-4146-a7b6-59815e2f5eb9.jpg'),(25,2500,100,'女裝','橘色花紋洋裝','bc8bb8f4-cec4-40e2-9231-1d48cbbb02d7.jpg'),(26,2600,100,'女裝','純白無袖洋裝','be45bed2-698e-4682-adbc-a57d9e32b551.jpg'),(27,2700,100,'女裝','淺黃碎花休閒服','be87a33e-c976-43f0-a68c-70c6c5d69fd0.jpg'),(28,2800,100,'男裝','灰色緊身長褲','c5b80c9f-17fc-4adb-b068-32f5602ed571.jpg'),(29,2900,100,'女裝','碎花長裙','da713ba7-3959-42bc-889a-efd66dce11d4.jpg'),(30,3000,100,'女裝','橘色細肩長裙','e605c919-467f-4d04-a38a-33159f0756d8.jpg');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-27 21:11:08
