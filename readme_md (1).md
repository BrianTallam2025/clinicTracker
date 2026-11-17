# Maternal & Child Health Clinic Tracker (MHC Tracker)

A lightweight Java Swing and MySQL desktop application built for rural Kenyan community health clinics. This app replaces paper registers to help nurses instantly track maternal antenatal care (ANC) visits, child immunizations, and critical appointment defaulters.

---

## 🚀 The Real-World Problem
In rural Kenyan dispensaries, community health nurses track patient visits using bulky paper booklets. When a mother misses an antenatal care visit or a baby misses a vaccine, defaults often go unnoticed for weeks, increasing health risks. 

**Our Solution:** A lightning-fast desktop tool featuring a one-click defaulter audit (`SHOW DEFAULTERS`) that highlights overdue patients in red, displays their contact details and village, and ensures secure local data storage compliant with the Kenyan Data Protection Act (DPA).

---

## 🛠️ Tech Stack
* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing (`JFrame`, `JTable`, `JComboBox`, `JOptionPane`)
* **Database:** MySQL
* **Database Connector:** MySQL JDBC Driver (`mysql-connector-java`)

---

## 📂 Project Structure & Division of Labor

The codebase is strictly modularized so that both team members can work in parallel without merge conflicts on the `main` branch.

```text
clinic_tracker/
│
├── Db.java                  (Partner A - Tallam) - MySQL JDBC Connection handler
├── Patient.java             (Partner A - Tallam) - POJO model class (Getters/Setters)
├── PatientDAO.java          (Partner A - Tallam) - SQL queries (CRUD, search, defaulters, stock count)
├── ClinicForm.java          (Partner B - You)    - Java Swing GUI, Table Renderer, Validation
└── App.java                 (Partner B - You)    - Main application entry point
```

---

## 📊 Database Schema (MySQL)

Run the following SQL script to initialize your database:

```sql
CREATE DATABASE clinic_tracker;
USE clinic_tracker;

CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    patient_type ENUM('Pregnant Mother', 'Child') NOT NULL,
    phone VARCHAR(20) NOT NULL,
    village VARCHAR(50) NOT NULL,
    date_registered DATE NOT NULL,
    last_visit DATE NOT NULL,
    next_visit DATE NOT NULL,
    status ENUM('Active', 'Defaulter', 'Completed') DEFAULT 'Active'
);
```

---

## 💡 Proposed Future Improvements (Phase 2 & Demo Pitch)

1. **Automated WhatsApp / SMS Reminders:** Trigger direct reminder links (`wa.me/...`) for defaulters straight from the table interface.
2. **Offline-First SQLite Sync:** Abstract the JDBC layer to support local SQLite files on flash drives for Community Health Workers (CHWs) working in areas with power or internet outages.
3. **Vaccine Stock Forecasting:** Link upcoming patient counts with clinic inventory limits to issue automated stock shortage warnings.

---

## 👥 Team Collaboration Guidelines
* **Tonight:** Work independently on assigned files (Backend vs. Frontend UI shell).
* **Tomorrow Morning:** Integrate DAO methods into GUI button event listeners.
* **Tomorrow Afternoon:** Code walk-through swap—both partners must be able to explain any section of the codebase during grading.