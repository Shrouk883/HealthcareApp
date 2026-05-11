import java.sql.*;

public class MedicationOperations {

    //INSERT MEDICATION
    public static boolean insertMedication(int medicationId,
                                           String medicationName) {
        String sql =
                "INSERT INTO MEDICATION (MEDICATION_ID, MEDICATION_NAME) " +
                        "VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, medicationId);
            ps.setString(2, medicationName);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Medication inserted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }

        return false;
    }

    //UPDATE MEDICATION
    public static boolean updateMedication(int medicationId,
                                           String medicationName) {

        String sql =
                "UPDATE MEDICATION " +
                        "SET MEDICATION_NAME = ? " +
                        "WHERE MEDICATION_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, medicationName);
            ps.setInt(2, medicationId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Medication updated successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }

        return false;
    }

    //DELETE MEDICATION
    public static boolean deleteMedication(int medicationId) {

        String sql =
                "DELETE FROM MEDICATION " +
                        "WHERE MEDICATION_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, medicationId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Medication deleted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
        }

        return false;
    }

    //SELECT ALL MEDICATIONS
    public static void selectMedications() {

        String sql =
                "SELECT MEDICATION_ID, MEDICATION_NAME " +
                        "FROM MEDICATION " +
                        "ORDER BY MEDICATION_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== ALL MEDICATIONS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("MEDICATION_ID") + " | " +
                                rs.getString("MEDICATION_NAME")
                );
            }

        } catch (SQLException e) {
            System.err.println("Select failed: " + e.getMessage());
        }
    }

    //SEARCH MEDICATION
    public static void searchMedicationByName(String name) {

        String sql =
                "SELECT * FROM MEDICATION " +
                        "WHERE MEDICATION_NAME LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== SEARCH RESULTS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("MEDICATION_ID") + " | " +
                                rs.getString("MEDICATION_NAME")
                );
            }

        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }
}