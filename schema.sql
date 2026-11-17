CREATE DATABASE IF NOT EXISTS clinic_tracker;
USE clinic_tracker;

CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    patient_type ENUM('Pregnant Mother', 'Child') NOT NULL,
    phone VARCHAR(20) NOT NULL,
    village VARCHAR(50) NOT NULL,
    date_registered DATE NOT NULL,
    last_visit DATE NOT NULL,
    next_visit DATE NOT NULL,
    status ENUM('Active', 'Defaulter', 'Completed') NOT NULL DEFAULT 'Active'
);