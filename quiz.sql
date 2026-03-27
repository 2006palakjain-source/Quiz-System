CREATE DATABASE online_exam;
USE online_exam;

CREATE TABLE questions (
  id INT PRIMARY KEY AUTO_INCREMENT,
  text TEXT NOT NULL,
  option_a VARCHAR(255) NOT NULL,
  option_b VARCHAR(255) NOT NULL,
  option_c VARCHAR(255) NOT NULL,
  option_d VARCHAR(255) NOT NULL,
  correct_option CHAR(1) NOT NULL
);

INSERT INTO questions (text, option_a, option_b, option_c, option_d, correct_option) VALUES
('What is the size of int in Java?', '2 bytes', '4 bytes', '8 bytes', 'Depends on OS', 'B'),
('Which keyword is used to inherit a class in Java?', 'implement', 'extends', 'inherits', 'super', 'B'),
('Which is a valid access modifier in Java?', 'public', 'package', 'friend', 'internal', 'A');