import java.sql.*;

public class BranchOperations {

    // insert bracnh
    public static boolean insertBranch(int branchId, String address, String contactDetails) {

        String sql = "INSERT INTO BRANCH (BRANCH_ID_, [_ADDRESS], CONTACT_DETAILS) " + "VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setString(2, address);
            ps.setString(3, contactDetails);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Branch inserted successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("insertBranch failed: " + e.getMessage());
        }
        return false;
    }

    // update branch
    public static boolean updateBranch(int branchId, String address, String contactDetails) {

        String sql = "UPDATE BRANCH " + "SET [_ADDRESS] = ?, CONTACT_DETAILS = ? " + "WHERE BRANCH_ID_ = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, address);
            ps.setString(2, contactDetails);
            ps.setInt(3, branchId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Branch updated successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("updateBranch failed: " + e.getMessage());
        }

        return false;
    }

    // delete branch
    public static boolean deleteBranch(int branchId) {

        String sql = "DELETE FROM BRANCH WHERE BRANCH_ID_ = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, branchId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Branch deleted successfully.");
              
                return true;
            }

        } catch (SQLException e) {
            System.err.println("deleteBranch failed: " + e.getMessage());
        }
        return false;
    }

    // select all
    public static void selectBranches() {

        String sql = "SELECT BRANCH_ID_, [_ADDRESS], CONTACT_DETAILS " +"FROM BRANCH " + "ORDER BY BRANCH_ID_";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== ALL BRANCHES =====");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("BRANCH_ID_") + " | " + rs.getString("_ADDRESS") + " | " + rs.getString("CONTACT_DETAILS")
                );
            }

        } catch (SQLException e) {
            System.err.println("selectBranches failed: " + e.getMessage());
        }
    }
}
