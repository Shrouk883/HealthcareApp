import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.stage.Stage;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Healthcare Network Management System");

        // Title
        Label titleLabel = new Label("THE MULTI-SPECIALTY HEALTHCARE NETWORK");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitleLabel = new Label("Main Menu");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");

        // Buttons
        Button btnPatients = new Button("Manage Patients");
        Button btnDoctors = new Button("Manage Doctors");
        Button btnAppointments = new Button("Manage Appointments");
        Button btnBranches = new Button("Manage Branches");
        Button btnMedications = new Button("Manage Medications");
        Button btnDiagnosis = new Button("Manage Diagnosis & Treatment");
        Button btnReports = new Button("View Reports & Analytics");
        Button btnExit = new Button("Exit");

        // Style buttons
        String buttonStyle = "-fx-font-size: 14px; -fx-pref-width: 250px; -fx-pref-height: 40px; -fx-background-color: #3498db; -fx-text-fill: white;";
        btnPatients.setStyle(buttonStyle);
        btnDoctors.setStyle(buttonStyle);
        btnAppointments.setStyle(buttonStyle);
        btnBranches.setStyle(buttonStyle);
        btnMedications.setStyle(buttonStyle);
        btnDiagnosis.setStyle(buttonStyle);
        btnReports.setStyle(buttonStyle);
        btnExit.setStyle("-fx-font-size: 14px; -fx-pref-width: 250px; -fx-pref-height: 40px; -fx-background-color: #e74c3c; -fx-text-fill: white;");

        // Hover effects
        btnPatients.setOnMouseEntered(e -> btnPatients.setStyle("-fx-font-size: 14px; -fx-pref-width: 250px; -fx-pref-height: 40px; -fx-background-color: #2980b9; -fx-text-fill: white;"));
        btnPatients.setOnMouseExited(e -> btnPatients.setStyle(buttonStyle));

        // Layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1;");
        layout.getChildren().addAll(titleLabel, subtitleLabel,
                new Separator(), btnPatients, btnDoctors, btnAppointments,
                btnBranches, btnMedications, btnDiagnosis, btnReports,
                new Separator(), btnExit);

        // Button actions
        btnPatients.setOnAction(e -> new PatientGUI().show());
        btnDoctors.setOnAction(e -> new DoctorGUI().show());
        btnAppointments.setOnAction(e -> new AppointmentGUI().show());
        btnBranches.setOnAction(e -> new BranchGUI().show());
        btnMedications.setOnAction(e -> new MedicationGUI().show());
        btnDiagnosis.setOnAction(e -> new DiagnosisGUI().show());
        btnReports.setOnAction(e -> new ReportGUI().show());
        btnExit.setOnAction(e -> primaryStage.close());

        Scene scene = new Scene(layout, 500, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}