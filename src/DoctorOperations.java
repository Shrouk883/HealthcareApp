import java.sql.*;

public class DoctorOperations {

    //INSERT DOCTOR
    public static boolean insertDoctor(int doctorId,
                                       int branchId,
                                       String name,
                                       String expertise) {
        String sql =
                "INSERT INTO DOCTOR " +
                        "(DOCTOR_ID, BRANCH_ID_, NAME, EXPERTISE) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setInt(2, branchId);
            ps.setString(3, name);
            ps.setString(4, expertise);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Doctor inserted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }

        return false;
    }

    //UPDATE DOCTOR
    public static boolean updateDoctor(int doctorId,
                                       String name,
                                       String expertise) {

        String sql =
                "UPDATE DOCTOR " +
                        "SET NAME = ?, EXPERTISE = ? " +
                        "WHERE DOCTOR_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, expertise);
            ps.setInt(3, doctorId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Doctor updated successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Update failed: " + e.getMessage());
        }

        return false;
    }

    //DELETE DOCTOR
    public static boolean deleteDoctor(int doctorId) {

        String sql =
                "DELETE FROM DOCTOR " +
                        "WHERE DOCTOR_ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Doctor deleted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
        }

        return false;
    }

    //SELECT ALL DOCTORS
    public static void selectDoctors() {

        String sql =
                "SELECT DOCTOR_ID, BRANCH_ID_, NAME, EXPERTISE " +
                        "FROM DOCTOR " +
                        "ORDER BY DOCTOR_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== ALL DOCTORS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("DOCTOR_ID") + " | " +
                                rs.getInt("BRANCH_ID_") + " | " +
                                rs.getString("NAME") + " | " +
                                rs.getString("EXPERTISE")
                );
            }

        } catch (SQLException e) {
            System.err.println("Select failed: " + e.getMessage());
        }
    }

    //SEARCH DOCTOR BY NAME
    public static void searchDoctorByName(String doctorName) {

        String sql =
                "SELECT * FROM DOCTOR " +
                        "WHERE NAME LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + doctorName + "%");

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== SEARCH RESULTS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("DOCTOR_ID") + " | " +
                                rs.getString("NAME") + " | " +
                                rs.getString("EXPERTISE")
                );
            }

        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    //SEARCH BY EXPERTISE
    public static void searchByExpertise(String expertise) {

        String sql =
                "SELECT * FROM DOCTOR " +
                        "WHERE EXPERTISE LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + expertise + "%");

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== DOCTORS BY EXPERTISE =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("DOCTOR_ID") + " | " +
                                rs.getString("NAME") + " | " +
                                rs.getString("EXPERTISE")
                );
            }

        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }
     // SEARCH BY BRANCH LOCATION
    public static void searchByBranchLocation(String location) {

        String sql =
                "SELECT D.DOCTOR_ID, D.NAME, D.EXPERTISE, B._ADDRESS FROM DOCTOR D " +
                        "JOIN BRANCH B ON D.BRANCH_ID_ = B.BRANCH_ID_ " +
                        "WHERE B._ADDRESS LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + location + "%");

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== DOCTORS BY BRANCH LOCATION =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("DOCTOR_ID") + " | " +
                                rs.getString("NAME") + " | " +
                                rs.getString("EXPERTISE") + " | " +
                                rs.getString("_ADDRESS")
                );
            }

        } catch (SQLException e) {
            System.err.println("Search by branch location failed: " + e.getMessage());
        }
    }
}
