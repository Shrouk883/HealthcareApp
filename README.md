# 🏥 Multi-Specialty Healthcare Network Management System

A full-stack desktop application built as part of a **Database Systems** course project.  
The system manages a regional healthcare network — handling branches, doctors, patients, appointments, diagnoses, and medications through a clean JavaFX GUI connected to a Microsoft SQL Server database via pure JDBC.

---

## 📋 Project Overview

| Layer | Technology |
|---|---|
| Database Design | PowerDesigner (Conceptual + Physical ERD) |
| Database | Microsoft SQL Server |
| Backend | Java (pure JDBC, no ORM) |
| Frontend | JavaFX |
| Version Control | Git + GitHub |
| IDE | IntelliJ IDEA |

---

## 🗂️ Database Design

### Entities
- **Branch** — Branch_Id, Address, Contact_Details
- **Doctor** — Doctor_Id, Name, Expertise, Branch_Id (FK)
- **Patient** — Patient_Id, Name, Demographics, History
- **Appointment** — Appointment_Id, Date, Time, Doctor_Id (FK), Patient_Id (FK), Diagnosis_Id (FK)
- **Diagnosis** — Diagnosis_Id, Description, Appointment_Id (FK)
- **Medication** — Medication_Id, Medication_Name
- **Requires** *(junction table)* — Diagnosis_Id (FK), Medication_Id (FK), Dosage, Duration

### Relationships
| Relationship | Type | Description |
|---|---|---|
| Branch → Doctor | One to Many | A doctor belongs to one branch |
| Doctor → Appointment | One to Many | A doctor conducts many appointments |
| Patient → Appointment | One to Many | A patient attends many appointments |
| Appointment → Diagnosis | One to One | Each appointment has exactly one diagnosis |
| Diagnosis ↔ Medication | Many to Many | Via Requires junction table |

---

## ⚙️ Application Features

### CRUD Operations
- ✅ **INSERT** — Add new patients and doctors
- ✅ **UPDATE** — Update patient info and doctor expertise
- ✅ **DELETE** — Remove patients and medications
- ✅ **SELECT** — View all records from any table
- ✅ **JOIN** — View appointments with doctor, patient, and diagnosis info

### 6 Inquiry Queries
1. Which medical specialty had the highest number of consultations last month?
2. List all doctors who did not conduct any consultations last month
3. Who was the patient that received the highest variety of medications last month?
4. Identify the branch that hosted the maximum number of distinct patients last month
5. What are the diagnosis details for all consultations held at a specific branch last month?
6. For each patient, retrieve their full profile and total number of prescriptions issued

---

## 🖥️ GUI Screens
- **Main Menu** — Navigation to all sections
- **Patient Screen** — Insert, update, delete, view patients
- **Doctor Screen** — Insert, update, view doctors
- **Appointment Screen** — View appointments with JOIN
- **Branch Screen** — View all branches
- **Medication Screen** — Insert, delete, view medications
- **Diagnosis Screen** — Insert diagnosis and treatment plans
- **Reports Screen** — Run all 6 inquiry queries

---

## 🚀 How to Run

### Requirements
- Java JDK 17 or 21
- JavaFX SDK 21
- Microsoft SQL Server (or SQL Server Express)
- IntelliJ IDEA
- Microsoft JDBC Driver for SQL Server

### Step 1 — Clone the Repository
```bash
git clone https://github.com/Shrouk883/HealthcareNetworkApp.git
cd HealthcareNetworkApp
```

### Step 2 — Set Up the Database
- Open **SQL Server Management Studio (SSMS)**
- Open the file `database/HealthcareNetworkDB.sql`
- Execute the script — this creates all tables and inserts sample data

### Step 3 — Configure Database Connection
Open `src/DBConnection.java` and update the connection string:
```java
private static final String URL =
    "jdbc:sqlserver://localhost\\SQLEXPRESS;" +
    "databaseName=HealthcareNetworkDB;" +
    "integratedSecurity=true;" +
    "trustServerCertificate=true;";
```
Change `localhost\\SQLEXPRESS` to match your SQL Server instance name.

### Step 4 — Add Libraries in IntelliJ
- Go to **File → Project Structure → Libraries**
- Add **JavaFX SDK lib folder** (all .jar files)
- Add **mssql-jdbc.jar**

### Step 5 — Set VM Options
- Go to **Run → Edit Configurations**
- Select **MainGUI**
- Add VM options:
```
--module-path "YOUR_JAVAFX_PATH\lib" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics
```
Replace `YOUR_JAVAFX_PATH` with your actual JavaFX SDK path.

### Step 6 — Run
- Right click **MainGUI.java** in the project panel
- Click **Run 'MainGUI.main()'**

---

## 📁 Project Structure
```
HealthcareNetworkApp/
├── src/
│   ├── MainGUI.java               ← App entry point
│   ├── DBConnection.java          ← Database connection
│   ├── PatientOperations.java     ← Patient CRUD
│   ├── DoctorOperations.java      ← Doctor CRUD
│   ├── AppointmentOperations.java ← Appointment CRUD
│   ├── BranchOperations.java      ← Branch CRUD
│   ├── MedicationOperations.java  ← Medication CRUD
│   ├── DiagnosisOperations.java   ← Diagnosis CRUD
│   ├── QueryOperations.java       ← 6 Inquiry queries
│   ├── PatientGUI.java            ← Patient screen
│   ├── DoctorGUI.java             ← Doctor screen
│   ├── AppointmentGUI.java        ← Appointment screen
│   ├── BranchGUI.java             ← Branch screen
│   ├── MedicationGUI.java         ← Medication screen
│   ├── DiagnosisGUI.java          ← Diagnosis screen
│   └── ReportGUI.java             ← Queries screen
├── database/
│   └── HealthcareNetworkDB.sql    ← Full database script
├── .gitignore
└── README.md
```

---

## 🛠️ Technologies Used
- **Java 21**
- **JavaFX 21**
- **Microsoft SQL Server**
- **Microsoft JDBC Driver 13.x**
- **PowerDesigner** — ERD Design
- **IntelliJ IDEA**
- **Git & GitHub**

---

## 📸 Screenshots
<img width="850" height="711" alt="Screenshot 2026-10-04 202608" src="https://github.com/user-attachments/assets/934e0331-ecfd-4351-9651-4b7c5b66dd3e" />
<img width="996" height="678" alt="Screenshot 2026-10-04 202710" src="https://github.com/user-attachments/assets/ec607310-f7ca-4a1c-a8cd-a980ee353f47" />
<img width="905" height="693" alt="Screenshot 2026-10-04 202747" src="https://github.com/user-attachments/assets/ee54f2fc-ea93-46d8-8ed3-cd08c0087c3a" />
<img width="865" height="705" alt="Screenshot 2026-10-04 202913" src="https://github.com/user-attachments/assets/49afebb4-12fd-4cea-8403-ecb18380604f" />

---

## 📄 License
This project was built for academic purposes as part of a Database Systems course.
