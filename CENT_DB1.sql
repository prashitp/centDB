CREATE DATABASE IF NOT EXISTS `CENT_DB1`
USE `CENT_DB1`;


--
-- Table structure for table `BIRDS`
--


DROP TABLE IF EXISTS `BIRDS`
CREATE TABLE `BIRDS` (
`BIRD_ID` INTEGER,
`COMMON_NAME` VARCHAR,
`SCIENTIFIC_NAME` VARCHAR,
PRIMARY KEY (`BIRD_ID`)
);


--
-- Table structure for table `MOVIES`
--


DROP TABLE IF EXISTS `MOVIES`
CREATE TABLE `MOVIES` (
`ID` INTEGER,
`NAME` VARCHAR,
`YEAR` VARCHAR,
`RANK` VARCHAR,
PRIMARY KEY (`ID`)
);


--
-- Dumping data for table `MOVIES`
--

LOCK TABLES `MOVIES` WRITE;
INSERT INTO `MOVIES` VALUES 
UNLOCK TABLES;

--
-- Dumping data for table `BIRDS`
--

LOCK TABLES `BIRDS` WRITE;
INSERT INTO `BIRDS` VALUES ('BirdCommonName1','BirdScientificName1'),('BirdCommonName2','BirdScientificName2'),('BirdCommonName3','BirdScientificName3'),('BirdCommonName4'),('','BirdScientificName5'),();
UNLOCK TABLES;