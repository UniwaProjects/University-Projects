-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 20, 2017 at 03:45 PM
-- Server version: 10.1.10-MariaDB
-- PHP Version: 7.0.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ecoursesdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `administrator`
--

CREATE TABLE `administrator` (
  `admin_id` int(11) NOT NULL,
  `username` varchar(12) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `password` varchar(12) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `administrator`
--

INSERT INTO `administrator` (`admin_id`, `username`, `password`) VALUES
(1, 'admin', 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses` (
  `course_id` int(4) NOT NULL,
  `prof_id` int(4) NOT NULL,
  `greek_title` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `english_title` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `edu_level` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `semester` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`course_id`, `prof_id`, `greek_title`, `english_title`, `edu_level`, `semester`) VALUES
(1, 1, 'Diktya 1', 'Networks 1', 'Undergraduate', 1),
(3, 1, 'Diktya 2', 'Networks 2 ', 'Undergraduate', 2),
(4, 1, 'Diktya 3', 'Networks 3', 'Undergraduate', 3),
(5, 1, 'Diktya 4', 'Networks 4', 'Undergraduate', 4),
(6, 2, 'Diktyakes Efarmoges 1', 'Network Applications 1', 'Undergraduate', 5),
(7, 2, 'Diktyakes Efarmoges 2', 'Network Applications 2', 'Undergraduate', 6),
(8, 2, 'Diktyakes Efarmoges 3', 'Network Applications 3', 'Undergraduate', 7);

-- --------------------------------------------------------

--
-- Table structure for table `course_outcomes`
--

CREATE TABLE `course_outcomes` (
  `course_id` int(4) NOT NULL,
  `outcome_id` varchar(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `course_outcomes`
--

INSERT INTO `course_outcomes` (`course_id`, `outcome_id`) VALUES
(1, 'NC.I.1'),
(3, 'NC.I.2'),
(4, 'NC.I.3'),
(5, 'NC.I.4'),
(6, 'NC.NA.1'),
(7, 'NC.NA.2'),
(8, 'NC.NA.3');

-- --------------------------------------------------------

--
-- Table structure for table `course_required_courses`
--

CREATE TABLE `course_required_courses` (
  `course_id` int(4) NOT NULL,
  `req_course_id` int(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `course_required_courses`
--

INSERT INTO `course_required_courses` (`course_id`, `req_course_id`) VALUES
(3, 1),
(4, 1),
(4, 3),
(5, 1),
(5, 3),
(5, 4),
(6, 1),
(6, 3),
(6, 4),
(6, 5),
(7, 1),
(7, 3),
(7, 4),
(7, 5),
(7, 6),
(8, 1),
(8, 3),
(8, 4),
(8, 5),
(8, 6),
(8, 7);

-- --------------------------------------------------------

--
-- Table structure for table `course_required_outcomes`
--

CREATE TABLE `course_required_outcomes` (
  `course_id` int(4) NOT NULL,
  `outcome_id` varchar(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `course_required_outcomes`
--

INSERT INTO `course_required_outcomes` (`course_id`, `outcome_id`) VALUES
(3, 'NC.I.1'),
(4, 'NC.I.1'),
(4, 'NC.I.2'),
(5, 'NC.I.1'),
(5, 'NC.I.2'),
(5, 'NC.I.3'),
(6, 'NC.I.1'),
(6, 'NC.I.2'),
(6, 'NC.I.3'),
(6, 'NC.I.4'),
(7, 'NC.I.1'),
(7, 'NC.I.2'),
(7, 'NC.I.3'),
(7, 'NC.I.4'),
(7, 'NC.NA.1'),
(8, 'NC.I.1'),
(8, 'NC.I.2'),
(8, 'NC.I.3'),
(8, 'NC.I.4'),
(8, 'NC.NA.1'),
(8, 'NC.NA.2');

-- --------------------------------------------------------

--
-- Table structure for table `favorite_courses`
--

CREATE TABLE `favorite_courses` (
  `stud_id` int(4) NOT NULL,
  `course_id` int(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `favorite_outcomes`
--

CREATE TABLE `favorite_outcomes` (
  `stud_id` int(4) NOT NULL,
  `outcome_id` varchar(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `learning_outcomes`
--

CREATE TABLE `learning_outcomes` (
  `outcome_id` varchar(11) NOT NULL,
  `field` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `category` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `number` int(4) NOT NULL,
  `description` varchar(70) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `mastery_level` varchar(15) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `learning_outcomes`
--

INSERT INTO `learning_outcomes` (`outcome_id`, `field`, `category`, `number`, `description`, `mastery_level`) VALUES
('NC.I.1', 'Network and Communication', 'Introduction', 1, 'Articulate the organization of the Internet.', 'Familiarity'),
('NC.I.2', 'Network and Communication', 'Introduction', 2, 'List and define the appropriate network terminology.', 'Familiarity'),
('NC.I.3', 'Network and Communication', 'Introduction', 3, 'Describe the layered structure of a typical networked architecture.', 'Familiarity'),
('NC.I.4', 'Network and Communication', 'Introduction', 4, 'Identify the different types of complexity in a network.', 'Familiarity'),
('NC.NA.1', 'Network and Communication', 'Networked Applications', 1, 'List the differences and the relations between names and addresses.', 'Familiarity'),
('NC.NA.2', 'Network and Communication', 'Networked Applications', 2, 'Define the principles behind naming schemes and resource location.', 'Familiarity'),
('NC.NA.3', 'Network and Communication', 'Networked Applications', 3, 'Implement a simple client-server socket-based application.', 'Usage');

-- --------------------------------------------------------

--
-- Table structure for table `professors`
--

CREATE TABLE `professors` (
  `prof_id` int(4) NOT NULL,
  `firstname` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `lastname` varchar(45) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `edu_level` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `username` varchar(12) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `password` varchar(12) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `account_activated` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `professors`
--

INSERT INTO `professors` (`prof_id`, `firstname`, `lastname`, `edu_level`, `username`, `password`, `account_activated`) VALUES
(1, 'Dimitris', 'Dimitropoulos', 'Professor', 'prof1', '123456', 1),
(2, 'Giannis', 'Giannopoulos', 'Associate Professor', 'prof2', '123456', 1);

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `stud_id` int(4) NOT NULL,
  `username` varchar(12) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL,
  `password` varchar(12) CHARACTER SET utf8 COLLATE utf8_unicode_520_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`stud_id`, `username`, `password`) VALUES
(1, 'stud1', '123456'),
(2, 'stud2', '123456');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `administrator`
--
ALTER TABLE `administrator`
  ADD PRIMARY KEY (`admin_id`);

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`course_id`),
  ADD KEY `prof_id` (`prof_id`);

--
-- Indexes for table `course_outcomes`
--
ALTER TABLE `course_outcomes`
  ADD KEY `course_id` (`course_id`),
  ADD KEY `goal_id` (`outcome_id`);

--
-- Indexes for table `course_required_courses`
--
ALTER TABLE `course_required_courses`
  ADD KEY `course_id` (`course_id`),
  ADD KEY `req_course_id` (`req_course_id`);

--
-- Indexes for table `course_required_outcomes`
--
ALTER TABLE `course_required_outcomes`
  ADD KEY `course_id` (`course_id`),
  ADD KEY `goal_id` (`outcome_id`);

--
-- Indexes for table `favorite_courses`
--
ALTER TABLE `favorite_courses`
  ADD KEY `stud_id` (`stud_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `favorite_outcomes`
--
ALTER TABLE `favorite_outcomes`
  ADD KEY `stud_id` (`stud_id`),
  ADD KEY `outcome_id` (`outcome_id`);

--
-- Indexes for table `learning_outcomes`
--
ALTER TABLE `learning_outcomes`
  ADD PRIMARY KEY (`outcome_id`),
  ADD UNIQUE KEY `outcome_id` (`outcome_id`);

--
-- Indexes for table `professors`
--
ALTER TABLE `professors`
  ADD PRIMARY KEY (`prof_id`),
  ADD UNIQUE KEY `prof_id` (`prof_id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`stud_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `administrator`
--
ALTER TABLE `administrator`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
  MODIFY `course_id` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `professors`
--
ALTER TABLE `professors`
  MODIFY `prof_id` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `stud_id` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `courses`
--
ALTER TABLE `courses`
  ADD CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`prof_id`) REFERENCES `professors` (`prof_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `course_outcomes`
--
ALTER TABLE `course_outcomes`
  ADD CONSTRAINT `course_outcomes_ibfk_4` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `course_outcomes_ibfk_5` FOREIGN KEY (`outcome_id`) REFERENCES `learning_outcomes` (`outcome_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `course_required_courses`
--
ALTER TABLE `course_required_courses`
  ADD CONSTRAINT `course_required_courses_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `course_required_courses_ibfk_2` FOREIGN KEY (`req_course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `course_required_outcomes`
--
ALTER TABLE `course_required_outcomes`
  ADD CONSTRAINT `course_required_outcomes_ibfk_2` FOREIGN KEY (`outcome_id`) REFERENCES `learning_outcomes` (`outcome_id`),
  ADD CONSTRAINT `course_required_outcomes_ibfk_3` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `favorite_courses`
--
ALTER TABLE `favorite_courses`
  ADD CONSTRAINT `favorite_courses_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `favorite_courses_ibfk_3` FOREIGN KEY (`stud_id`) REFERENCES `students` (`stud_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `favorite_outcomes`
--
ALTER TABLE `favorite_outcomes`
  ADD CONSTRAINT `favorite_outcomes_ibfk_4` FOREIGN KEY (`stud_id`) REFERENCES `students` (`stud_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `favorite_outcomes_ibfk_5` FOREIGN KEY (`outcome_id`) REFERENCES `learning_outcomes` (`outcome_id`) ON DELETE CASCADE ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
