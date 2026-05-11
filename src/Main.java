import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.ResultSet;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            var conn = DBConnection.getConnection();
            System.out.println(" connected to SQL Server!");
            conn.close();
        } catch (Exception e) {
            System.out.println(" Failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}