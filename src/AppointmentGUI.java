import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class AppointmentGUI {

    private TableView<ObservableList<String>> tableView = new TableView<>();
    private TextArea logArea = new TextArea();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Appointments");

        TextField txtAppointmentId = new TextField();
        txtAppointmentId.setPromptText("Appointment ID");
        TextField txtDiagnosisId = new TextField();
        txtDiagnosisId.setPromptText("Diagnosis ID");
        TextField txtDoctorId = new TextField();
        txtDoctorId.setPromptText("Doctor ID");
        TextField txtPatientId = new TextField();
        txtPatientId.setPromptText("Patient ID");
        DatePicker datePicker = new DatePicker();
        TextField txtTime = new TextField();
        txtTime.setPromptText("Time (HH:MM)");

        Button btnInsert = new Button("Add Appointment");
        Button btnUpdate = new Button("Update Doctor");
        Button btnDelete = new Button("Delete");
        Button btnRefresh = new Button("Refresh");

        btnInsert.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnUpdate.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnRefresh.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        logArea.setEditable(false);
        logArea.setPrefHeight(150);

        setupTable();

        btnInsert.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtAppointmentId.getText());
                int diagnosisId = Integer.parseInt(txtDiagnosisId.getText());
                int doctorId = Integer.parseInt(txtDoctorId.getText());
                int patientId = Integer.parseInt(txtPatientId.getText());
                String date = datePicker.getValue().toString();
                String time = txtTime.getText();

                String sql = "INSERT INTO APPOINTMENT (APPOINTMENT_ID, DIAGNOSIS_ID, DOCTOR_ID, PATIENT_ID_, DATE, TIME) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.setInt(2, diagnosisId);
                    ps.setInt(3, doctorId);
                    ps.setInt(4, patientId);
                    ps.setString(5, date);
                    ps.setString(6, time + ":00");
                    ps.executeUpdate();
                    logArea.appendText("✓ Appointment " + id + " added!\n");
                    refreshTable();
                    clearFields(txtAppointmentId, txtDiagnosisId, txtDoctorId, txtPatientId, txtTime);
                    datePicker.setValue(null);
                }
            } catch (SQLException ex) {
                logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
            } catch (Exception ex) {
                logArea.appendText("✗ Invalid input: " + ex.getMessage() + "\n");
            }
        });

        btnDelete.setOnAction(e -> {
            String selected = getSelectedId();
            if (selected != null) {
                try {
                    String sql = "DELETE FROM APPOINTMENT WHERE APPOINTMENT_ID = ?";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(selected));
                        ps.executeUpdate();
                        logArea.appendText("✓ Appointment " + selected + " deleted!\n");
                        refreshTable();
                    }
                } catch (SQLException ex) {
                    logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                }
            }
        });

        btnRefresh.setOnAction(e -> refreshTable());

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.add(new Label("Appointment ID:"), 0, 0);
        inputGrid.add(txtAppointmentId, 1, 0);
        inputGrid.add(new Label("Diagnosis ID:"), 0, 1);
        inputGrid.add(txtDiagnosisId, 1, 1);
        inputGrid.add(new Label("Doctor ID:"), 0, 2);
        inputGrid.add(txtDoctorId, 1, 2);
        inputGrid.add(new Label("Patient ID:"), 0, 3);
        inputGrid.add(txtPatientId, 1, 3);
        inputGrid.add(new Label("Date:"), 0, 4);
        inputGrid.add(datePicker, 1, 4);
        inputGrid.add(new Label("Time:"), 0, 5);
        inputGrid.add(txtTime, 1, 5);

        HBox buttonBox = new HBox(10, btnInsert, btnUpdate, btnDelete, btnRefresh);

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(
                new Label("APPOINTMENT MANAGEMENT"),
                inputGrid, buttonBox,
                new Label("Appointment List:"), tableView,
                new Label("Log:"), logArea
        );

        Scene scene = new Scene(mainLayout, 900, 700);
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    private void setupTable() {
        TableColumn<ObservableList<String>, String> colId = new TableColumn<>("Appt ID");
        TableColumn<ObservableList<String>, String> colDiagnosis = new TableColumn<>("Diagnosis ID");
        TableColumn<ObservableList<String>, String> colDoctor = new TableColumn<>("Doctor ID");
        TableColumn<ObservableList<String>, String> colPatient = new TableColumn<>("Patient ID");
        TableColumn<ObservableList<String>, String> colDate = new TableColumn<>("Date");
        TableColumn<ObservableList<String>, String> colTime = new TableColumn<>("Time");

        colId.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        colDiagnosis.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(1)));
        colDoctor.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(2)));
        colPatient.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(3)));
        colDate.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(4)));
        colTime.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(5)));

        tableView.getColumns().addAll(colId, colDiagnosis, colDoctor, colPatient, colDate, colTime);
        tableView.setPrefHeight(300);
    }

    private void refreshTable() {
        tableView.getItems().clear();
        String sql = "SELECT APPOINTMENT_ID, DIAGNOSIS_ID, DOCTOR_ID, PATIENT_ID_, CONVERT(DATE, DATE) AS DATE, TIME FROM APPOINTMENT ORDER BY APPOINTMENT_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("APPOINTMENT_ID")));
                row.add(String.valueOf(rs.getInt("DIAGNOSIS_ID")));
                row.add(String.valueOf(rs.getInt("DOCTOR_ID")));
                row.add(String.valueOf(rs.getInt("PATIENT_ID_")));
                row.add(rs.getString("DATE"));
                row.add(rs.getString("TIME"));
                tableView.getItems().add(row);
            }
            logArea.appendText("✓ Loaded " + tableView.getItems().size() + " appointments\n");

        } catch (SQLException e) {
            logArea.appendText("✗ DB Error: " + e.getMessage() + "\n");
        }
    }

    private String getSelectedId() {
        if (tableView.getSelectionModel().getSelectedItem() != null) {
            return tableView.getSelectionModel().getSelectedItem().get(0);
        }
        return null;
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }
}