# Maternal & Child Health Clinic Tracker (MHC Tracker)

## 🌍 Why This Project Is Important
In rural Kenyan community health units and dispensaries, healthcare workers and nurses often rely on bulky paper registers and booklets to track maternal antenatal care (ANC) visits and infant immunizations. When a pregnant mother misses a clinic check-up or a baby misses a critical vaccination, the default often goes unnoticed for weeks. In rural settings, distance, farming duties, and lack of reminders mean these delays can lead to severe, preventable health complications.

## 🎯 What It Does
The **MHC Tracker** is a lightweight, high-performance Java desktop application designed specifically for rural clinic nurses. Instead of forcing staff to manually leaf through paper logs, the app digitizes patient records and provides an instant **"Defaulter Audit"** feature. With a single click, the nurse can instantly isolate every patient who has missed their scheduled visit, displaying their name, phone number, village, and status.

## 💡 Why It Matters
* **Saves Lives Through Proactive Outreach:** Transforms healthcare delivery from reactive record-keeping to proactive community follow-up.
* **Solves a Real-World Problem:** Directly addresses logistical challenges faced by health workers in rural regions rather than replicating generic student or library management systems.
* **Data Privacy & Ethics:** Designed with local data protection and privacy standards in mind, aligning with the requirements of the **Kenyan Data Protection Act (DPA)** regarding sensitive medical records.

## ⚙️ How It Does It (Technical Architecture)
The application is built using a clean, robust Object-Oriented Programming (OOP) architecture separated into distinct layers:
* **Model (`Patient.java`):** Encapsulates patient data fields (ID, name, patient type, phone, village, registration date, last visit, next visit, and status).
* **Database & Persistence (`Db.java` & `PatientDAO.java`):** Connects to a MySQL database via JDBC and executes optimized SQL queries (`INSERT`, `UPDATE`, `DELETE`, search filters, and date-comparison queries like `WHERE next_visit < CURDATE()`).
* **User Interface (`ClinicForm.java`):** Built using Java Swing (`JFrame`, `JTable`, dropdowns, and custom cell renderers) to provide an intuitive, color-coded visual workflow (highlighting overdue patients in red and those due within 7 days in orange).

## Run the application

1. Install MySQL and the MySQL Connector/J driver.
2. Create the database and table by running `mysql -u root -p < schema.sql`.
3. If needed, set `CLINIC_DB_URL`, `CLINIC_DB_USER`, and `CLINIC_DB_PASSWORD` to configure the connection. Defaults are `jdbc:mysql://localhost:3306/clinic_tracker`, `root`, and an empty password.
4. Compile from the project directory:

   ```sh
   mkdir -p out
   javac -d out Db.java Patient.java PatientDAO.java src/ClinicForm.java src/App.java
   ```

5. Run with the MySQL Connector/J JAR on the classpath:

   ```sh
   java -cp "out:mysql-connector-j.jar" App
   ```

Dates in the form must use `YYYY-MM-DD`. Select a table row to edit or delete it. Search checks patient name, phone number, and village. The defaulter view shows non-completed patients whose next visit is before today; overdue rows are highlighted red and visits due within seven days are highlighted orange.