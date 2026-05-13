import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class DiagnosisGUI {

    private TableView<ObservableList<String>> diagnosisTable = new TableView<>();
    private TableView<ObservableList<String>> treatmentTable = new TableView<>();
    private TextArea logArea = new TextArea();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Diagnosis & Treatment Plans");

        TabPane tabPane = new TabPane();

        // Tab 1: Add Diagnosis
        Tab diagnosisTab = new Tab("Add Diagnosis");
        VBox diagnosisBox = new VBox(10);
        diagnosisBox.setPadding(new Insets(10));

        TextField txtDiagnosisId = new TextField();
        txtDiagnosisId.setPromptText("Diagnosis ID");
        TextField txtAppointmentId = new TextField();
        txtAppointmentId.setPromptText("Appointment ID");
        TextArea txtDescription = new TextArea();
        txtDescription.setPromptText("Description");
        txtDescription.setPrefHeight(100);

        Button btnAddDiagnosis = new Button("Add Diagnosis");
        Button btnRefreshDiagnosis = new Button("Refresh Diagnosis List");
        Button btnDeleteDiagnosis = new Button("Delete Selected Diagnosis");
        btnDeleteDiagnosis.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        HBox diagnosisButtonBox = new HBox(10, btnAddDiagnosis, btnRefreshDiagnosis, btnDeleteDiagnosis);

        diagnosisBox.getChildren().addAll(
                new Label("Diagnosis ID:"), txtDiagnosisId,
                new Label("Appointment ID:"), txtAppointmentId,
                new Label("Description:"), txtDescription,
                diagnosisButtonBox,
                new Label("Existing Diagnoses (Click to select):"), diagnosisTable
        );
        diagnosisTab.setContent(diagnosisBox);

        // Tab 2: Add Treatment Plan
        Tab treatmentTab = new Tab("Add Treatment Plan");
        VBox treatmentBox = new VBox(10);
        treatmentBox.setPadding(new Insets(10));

        TextField txtTreatDiagnosisId = new TextField();
        txtTreatDiagnosisId.setPromptText("Diagnosis ID");
        TextField txtMedicationId = new TextField();
        txtMedicationId.setPromptText("Medication ID");
        TextField txtDosage = new TextField();
        txtDosage.setPromptText("Dosage");
        TextField txtDuration = new TextField();
        txtDuration.setPromptText("Duration (days)");

        Button btnAddTreatment = new Button("Add Treatment Plan");
        Button btnUpdateTreatment = new Button("Update Treatment Plan");
        Button btnRefreshTreatment = new Button("Refresh Treatment Plans");
        Button btnDeleteTreatment = new Button("Delete Selected Treatment");
        btnDeleteTreatment.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        HBox treatmentButtonBox = new HBox(10, btnAddTreatment, btnUpdateTreatment, btnRefreshTreatment, btnDeleteTreatment);

        treatmentBox.getChildren().addAll(
                new Label("Diagnosis ID:"), txtTreatDiagnosisId,
                new Label("Medication ID:"), txtMedicationId,
                new Label("Dosage:"), txtDosage,
                new Label("Duration:"), txtDuration,
                treatmentButtonBox,
                new Label("Treatment Plans (Click to select):"), treatmentTable
        );
        treatmentTab.setContent(treatmentBox);

        tabPane.getTabs().addAll(diagnosisTab, treatmentTab);

        // Log area
        logArea.setEditable(false);
        logArea.setPrefHeight(100);

        // Setup tables
        setupDiagnosisTable();
        setupTreatmentTable();

        diagnosisTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedRow) -> {
            if (selectedRow != null) {
                txtDiagnosisId.setText(selectedRow.get(0));
                txtAppointmentId.setText(selectedRow.get(1));
                txtDescription.setText(selectedRow.get(2));
            }
        });

        treatmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedRow) -> {
            if (selectedRow != null) {
                txtTreatDiagnosisId.setText(selectedRow.get(0));
                txtMedicationId.setText(selectedRow.get(1));
                txtDosage.setText(selectedRow.get(2));
                txtDuration.setText(selectedRow.get(3));
            }
        });

        // Button actions
        btnAddDiagnosis.setOnAction(e -> {
            Connection con = null;

            try {
                int id = Integer.parseInt(txtDiagnosisId.getText());
                int apptId = Integer.parseInt(txtAppointmentId.getText());
                String desc = txtDescription.getText();

                con = DBConnection.getConnection();
                con.setAutoCommit(false);

                String insertDiagnosisSql = "INSERT INTO DIAGNOSIS (DIAGNOSIS_ID, APPOINTMENT_ID, DESCRIPTION) VALUES (?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(insertDiagnosisSql)) {
                    ps.setInt(1, id);
                    ps.setInt(2, apptId);
                    ps.setString(3, desc);
                    ps.executeUpdate();
                }

                String updateAppointmentSql = "UPDATE APPOINTMENT SET DIAGNOSIS_ID = ? WHERE APPOINTMENT_ID = ?";
                try (PreparedStatement ps = con.prepareStatement(updateAppointmentSql)) {
                    ps.setInt(1, id);
                    ps.setInt(2, apptId);

                    int rows = ps.executeUpdate();

                    if (rows == 0) {
                        con.rollback();
                        logArea.appendText("✗ No appointment found with ID " + apptId + "\n");
                        return;
                    }
                }

                con.commit();

                logArea.appendText("✓ Diagnosis " + id + " added and linked to appointment " + apptId + "!\n");
                refreshDiagnosisTable();
                clearDiagnosisFields(txtDiagnosisId, txtAppointmentId, txtDescription);
                diagnosisTable.getSelectionModel().clearSelection();

            } catch (SQLException ex) {
                try {
                    if (con != null) con.rollback();
                } catch (SQLException rollbackEx) {
                    logArea.appendText("✗ Rollback Error: " + rollbackEx.getMessage() + "\n");
                }

                logArea.appendText("✗ Error: " + ex.getMessage() + "\n");

            } catch (Exception ex) {
                try {
                    if (con != null) con.rollback();
                } catch (SQLException rollbackEx) {
                    logArea.appendText("✗ Rollback Error: " + rollbackEx.getMessage() + "\n");
                }

                logArea.appendText("✗ Invalid input\n");

            } finally {
                try {
                    if (con != null) {
                        con.setAutoCommit(true);
                        con.close();
                    }
                } catch (SQLException closeEx) {
                    logArea.appendText("✗ Close Error: " + closeEx.getMessage() + "\n");
                }
            }
        });

        btnDeleteDiagnosis.setOnAction(e -> {
            if (diagnosisTable.getSelectionModel().getSelectedItem() != null) {
                String id = diagnosisTable.getSelectionModel().getSelectedItem().get(0);
                try {
                    String sql = "DELETE FROM DIAGNOSIS WHERE DIAGNOSIS_ID = ?";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(id));
                        ps.executeUpdate();
                        logArea.appendText("✓ Diagnosis " + id + " deleted!\n");
                        refreshDiagnosisTable();
                        clearDiagnosisFields(txtDiagnosisId, txtAppointmentId, txtDescription);
                    }
                } catch (SQLException ex) {
                    logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                }
            } else {
                logArea.appendText("✗ Please select a diagnosis to delete\n");
            }
        });

        btnAddTreatment.setOnAction(e -> {
            try {
                int diagId = Integer.parseInt(txtTreatDiagnosisId.getText());
                int medId = Integer.parseInt(txtMedicationId.getText());
                String dosage = txtDosage.getText();
                int duration = Integer.parseInt(txtDuration.getText());

                String sql = "INSERT INTO REQUIRES (DIAGNOSIS_ID, MEDICATION_ID, DOSAGE, DURATION) VALUES (?, ?, ?, ?)";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, diagId);
                    ps.setInt(2, medId);
                    ps.setString(3, dosage);
                    ps.setInt(4, duration);
                    ps.executeUpdate();
                    logArea.appendText("✓ Treatment plan added!\n");
                    refreshTreatmentTable();
                    clearFields(txtTreatDiagnosisId, txtMedicationId, txtDosage, txtDuration);
                    treatmentTable.getSelectionModel().clearSelection();
                }
            } catch (SQLException ex) {
                logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
            } catch (Exception ex) {
                logArea.appendText("✗ Invalid input\n");
            }
        });

        btnUpdateTreatment.setOnAction(e -> {
            try {
                int diagId = Integer.parseInt(txtTreatDiagnosisId.getText());
                int medId = Integer.parseInt(txtMedicationId.getText());
                String dosage = txtDosage.getText();
                int duration = Integer.parseInt(txtDuration.getText());

                String sql = "UPDATE REQUIRES SET DOSAGE = ?, DURATION = ? WHERE DIAGNOSIS_ID = ? AND MEDICATION_ID = ?";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, dosage);
                    ps.setInt(2, duration);
                    ps.setInt(3, diagId);
                    ps.setInt(4, medId);

                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        logArea.appendText("✓ Treatment plan updated!\n");
                        refreshTreatmentTable();
                        clearFields(txtTreatDiagnosisId, txtMedicationId, txtDosage, txtDuration);
                        treatmentTable.getSelectionModel().clearSelection();
                    } else {
                        logArea.appendText("✗ No treatment plan found for Diagnosis " + diagId + " and Medication " + medId + "\n");
                    }
                }
            } catch (SQLException ex) {
                logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
            } catch (Exception ex) {
                logArea.appendText("✗ Invalid input\n");
            }
        });

        btnDeleteTreatment.setOnAction(e -> {
            if (treatmentTable.getSelectionModel().getSelectedItem() != null) {
                String diagId = treatmentTable.getSelectionModel().getSelectedItem().get(0);
                String medId = treatmentTable.getSelectionModel().getSelectedItem().get(1);
                try {
                    String sql = "DELETE FROM REQUIRES WHERE DIAGNOSIS_ID = ? AND MEDICATION_ID = ?";
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(diagId));
                        ps.setInt(2, Integer.parseInt(medId));
                        ps.executeUpdate();
                        logArea.appendText("✓ Treatment plan deleted!\n");
                        refreshTreatmentTable();
                        clearFields(txtTreatDiagnosisId, txtMedicationId, txtDosage, txtDuration);
                    }
                } catch (SQLException ex) {
                    logArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                }
            } else {
                logArea.appendText("✗ Please select a treatment plan to delete\n");
            }
        });

        btnRefreshDiagnosis.setOnAction(e -> {
            refreshDiagnosisTable();
            clearDiagnosisFields(txtDiagnosisId, txtAppointmentId, txtDescription);
            diagnosisTable.getSelectionModel().clearSelection();
        });

        btnRefreshTreatment.setOnAction(e -> {
            refreshTreatmentTable();
            clearFields(txtTreatDiagnosisId, txtMedicationId, txtDosage, txtDuration);
            treatmentTable.getSelectionModel().clearSelection();
        });

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(tabPane, new Label("Log:"), logArea);

        Scene scene = new Scene(mainLayout, 900, 750);
        stage.setScene(scene);
        stage.show();

        refreshDiagnosisTable();
        refreshTreatmentTable();
    }

    private void setupDiagnosisTable() {
        TableColumn<ObservableList<String>, String> colId = new TableColumn<>("Diagnosis ID");
        TableColumn<ObservableList<String>, String> colAppt = new TableColumn<>("Appointment ID");
        TableColumn<ObservableList<String>, String> colDesc = new TableColumn<>("Description");

        colId.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        colAppt.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(1)));
        colDesc.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(2)));

        diagnosisTable.getColumns().addAll(colId, colAppt, colDesc);
        diagnosisTable.setPrefHeight(200);
    }

    private void setupTreatmentTable() {
        TableColumn<ObservableList<String>, String> colDiag = new TableColumn<>("Diagnosis ID");
        TableColumn<ObservableList<String>, String> colMed = new TableColumn<>("Medication ID");
        TableColumn<ObservableList<String>, String> colDosage = new TableColumn<>("Dosage");
        TableColumn<ObservableList<String>, String> colDuration = new TableColumn<>("Duration");

        colDiag.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(0)));
        colMed.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(1)));
        colDosage.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(2)));
        colDuration.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().get(3)));

        treatmentTable.getColumns().addAll(colDiag, colMed, colDosage, colDuration);
        treatmentTable.setPrefHeight(200);
    }

    private void refreshDiagnosisTable() {
        diagnosisTable.getItems().clear();
        String sql = "SELECT DIAGNOSIS_ID, APPOINTMENT_ID, DESCRIPTION FROM DIAGNOSIS ORDER BY DIAGNOSIS_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("DIAGNOSIS_ID")));
                row.add(String.valueOf(rs.getInt("APPOINTMENT_ID")));
                row.add(rs.getString("DESCRIPTION"));
                diagnosisTable.getItems().add(row);
            }
            logArea.appendText("✓ Loaded " + diagnosisTable.getItems().size() + " diagnoses\n");

        } catch (SQLException e) {
            logArea.appendText("✗ DB Error: " + e.getMessage() + "\n");
        }
    }

    private void refreshTreatmentTable() {
        treatmentTable.getItems().clear();
        String sql = "SELECT DIAGNOSIS_ID, MEDICATION_ID, DOSAGE, DURATION FROM REQUIRES ORDER BY DIAGNOSIS_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("DIAGNOSIS_ID")));
                row.add(String.valueOf(rs.getInt("MEDICATION_ID")));
                row.add(rs.getString("DOSAGE"));
                row.add(String.valueOf(rs.getInt("DURATION")));
                treatmentTable.getItems().add(row);
            }
            logArea.appendText("✓ Loaded " + treatmentTable.getItems().size() + " treatment plans\n");

        } catch (SQLException e) {
            logArea.appendText("✗ DB Error: " + e.getMessage() + "\n");
        }
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) f.clear();
    }

    private void clearDiagnosisFields(TextField txtDiagnosisId, TextField txtAppointmentId, TextArea txtDescription) {
        txtDiagnosisId.clear();
        txtAppointmentId.clear();
        txtDescription.clear();
    }
}
