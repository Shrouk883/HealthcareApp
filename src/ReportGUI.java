import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import java.sql.*;

public class ReportGUI {

    private TextArea reportArea = new TextArea();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Reports & Analytics");

        reportArea.setEditable(false);
        reportArea.setPrefHeight(500);
        reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        Button btnTopSpecialty = new Button("Top Specialty Last Month");
        Button btnDoctorsNoConsult = new Button("Doctors with 0 Consultations");
        Button btnTopPatient = new Button("Patient with Highest Medication Variety");
        Button btnTopBranch = new Button("Branch with Most Patients");
        Button btnPrescriptionCount = new Button("Patients with Prescription Count");

        btnTopSpecialty.setOnAction(e -> runReport("TOP SPECIALTY LAST MONTH",
                "SELECT TOP 1 D.EXPERTISE, COUNT(A.APPOINTMENT_ID) AS Total " +
                        "FROM APPOINTMENT A JOIN DOCTOR D ON A.DOCTOR_ID = D.DOCTOR_ID " +
                        "GROUP BY D.EXPERTISE ORDER BY Total DESC"));

        btnDoctorsNoConsult.setOnAction(e -> runReport("DOCTORS WITH NO CONSULTATIONS",
                "SELECT DOCTOR_ID, NAME FROM DOCTOR WHERE DOCTOR_ID NOT IN " +
                        "(SELECT DISTINCT DOCTOR_ID FROM APPOINTMENT)"));

        btnTopPatient.setOnAction(e -> runReport("PATIENT WITH HIGHEST MEDICATION VARIETY",
                "SELECT TOP 1 P.NAME, COUNT(DISTINCT R.MEDICATION_ID) AS MedCount " +
                        "FROM PATIENT P JOIN APPOINTMENT A ON P.PATIENT_ID_ = A.PATIENT_ID_ " +
                        "JOIN DIAGNOSIS D ON A.DIAGNOSIS_ID = D.DIAGNOSIS_ID " +
                        "JOIN REQUIRES R ON D.DIAGNOSIS_ID = R.DIAGNOSIS_ID " +
                        "GROUP BY P.NAME ORDER BY MedCount DESC"));

        btnTopBranch.setOnAction(e -> runReport("BRANCH WITH MOST PATIENTS",
                "SELECT TOP 1 B._ADDRESS, COUNT(DISTINCT A.PATIENT_ID_) AS PatientCount " +
                        "FROM BRANCH B JOIN DOCTOR D ON B.BRANCH_ID_ = D.BRANCH_ID_ " +
                        "JOIN APPOINTMENT A ON D.DOCTOR_ID = A.DOCTOR_ID " +
                        "GROUP BY B._ADDRESS ORDER BY PatientCount DESC"));

        btnPrescriptionCount.setOnAction(e -> runReport("PATIENTS WITH PRESCRIPTION COUNT",
                "SELECT P.PATIENT_ID_, P.NAME, COUNT(R.MEDICATION_ID) AS Prescriptions " +
                        "FROM PATIENT P LEFT JOIN APPOINTMENT A ON P.PATIENT_ID_ = A.PATIENT_ID_ " +
                        "LEFT JOIN DIAGNOSIS D ON A.DIAGNOSIS_ID = D.DIAGNOSIS_ID " +
                        "LEFT JOIN REQUIRES R ON D.DIAGNOSIS_ID = R.DIAGNOSIS_ID " +
                        "GROUP BY P.PATIENT_ID_, P.NAME ORDER BY P.PATIENT_ID_"));

        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(
                new Label("REPORTS:"),
                btnTopSpecialty, btnDoctorsNoConsult, btnTopPatient, btnTopBranch, btnPrescriptionCount
        );

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(buttonBox, reportArea);

        Scene scene = new Scene(mainLayout, 900, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void runReport(String title, String sql) {
        reportArea.clear();
        reportArea.appendText("=== " + title + " ===\n\n");

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            // Print headers
            for (int i = 1; i <= colCount; i++) {
                reportArea.appendText(String.format("%-25s", meta.getColumnName(i)));
            }
            reportArea.appendText("\n" + "=".repeat(colCount * 25) + "\n");

            // Print data
            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    reportArea.appendText(String.format("%-25s", rs.getString(i)));
                }
                reportArea.appendText("\n");
            }

            reportArea.appendText("\n✓ Report loaded successfully\n");

        } catch (SQLException e) {
            reportArea.appendText("Error: " + e.getMessage() + "\n");
        }
    }
}