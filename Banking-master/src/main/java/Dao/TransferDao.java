package Dao;

import java.sql.*;

import static Util.DbConnection.getConnection;

public class TransferDao {

    // Method to process the transaction
    public boolean processTransaction(String fromAccNumber, String toAccNumber, double amount, int custId,String billerFirstName, String billerLastName) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get database connection
            con = getConnection();

            // Check if both accounts exist
            if (!checkAccountExist(con, fromAccNumber) || !checkAccountExist(con, toAccNumber)) {
                return false; // One or both accounts don't exist
            }

            // Check sender's balance
            double senderBalance = getAccountBalance(con, fromAccNumber);
            if (senderBalance < amount) {
                return false; // Not enough balance
            }

            // Generate transaction number (transNum)
            String transNum = generateTransNum(con);

            // Start transaction (disable auto-commit)
            con.setAutoCommit(false);

            // Deduct amount from sender's account
            updateAccountBalance(con, fromAccNumber, -amount);

            // Add amount to receiver's account
            updateAccountBalance(con, toAccNumber, amount);

            // Insert transaction record
            insertTransaction(con, transNum, "WEBPAY", "Online", amount, custId, toAccNumber,billerFirstName,billerLastName);

            // Insert biller record if not already exists
            insertBiller(con, toAccNumber,billerFirstName,billerLastName);

            // Commit the transaction
            con.commit();

            return true;

        } catch (SQLException e) {
            if (con != null) {
                con.rollback();  // Rollback transaction in case of error
            }
            throw new SQLException("Error processing transaction", e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);  // Re-enable auto-commit
                con.close();
            }
        }
    }

    // Method to check if the account exists
    private boolean checkAccountExist(Connection con, String accNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM account WHERE accNumber = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Method to get the account balance
    private double getAccountBalance(Connection con, String accNumber) throws SQLException {
        String query = "SELECT balance FROM account WHERE accNumber = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }
        return 0.0; // Return 0 if account not found
    }

    // Method to update account balance
    private void updateAccountBalance(Connection con, String accNumber, double amount) throws SQLException {
        String query = "UPDATE account SET balance = balance + ? WHERE accNumber = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDouble(1, amount);
            ps.setString(2, accNumber);
            ps.executeUpdate();
        }
    }

    // Method to generate transaction number (transNum)
    private String generateTransNum(Connection con) throws SQLException {
        String query = "SELECT CONCAT('TRANS-', LPAD(COALESCE(MAX(SUBSTRING(transNum, 7)), 0) + 1, 6, '0')) AS transNum FROM transaction";
        try (PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("transNum");
            }
        }
        return null; // Return null if transaction number could not be generated
    }

    // Method to insert transaction record
    private void insertTransaction(Connection con, String transNum, String transType, String transMode, double amount, int custId, String billerAccNumber, String billerFirstName, String billerLastName) throws SQLException {
        // Insert or retrieve biller and get the billerId
        int billerId = insertBiller(con, billerAccNumber, billerFirstName, billerLastName);

        // Prepare the query for transaction insertion
        String transactionQuery = "INSERT INTO transaction (transNum, transType, transMode, transDate, amount, transStatus, custId, billerId) VALUES (?, ?, ?, NOW(), ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(transactionQuery)) {
            ps.setString(1, transNum);
            ps.setString(2, transType);
            ps.setString(3, transMode);
            ps.setDouble(4, amount);
            ps.setInt(5, 1);  // Status = 1 (success)
            ps.setInt(6, custId);
            ps.setInt(7, billerId);  // Use the returned biller ID
            ps.executeUpdate();
        }
    }


    // Method to get biller ID based on the account number
    private int getBillerId(Connection con, String billerAccNumber) throws SQLException {
        String query = "SELECT billerId FROM biller WHERE bAccNum = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, billerAccNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("billerId");
                }
            }
        }
        return 0; // Return 0 if biller ID is not found
    }

    // Method to insert biller (if not already exist)
    private int insertBiller(Connection con, String billerAccNumber, String billerFirstName, String billerLastName) throws SQLException {
        String query = "INSERT INTO biller (bFname, bLname, bAccNum) SELECT ?, ?, ? FROM dual WHERE NOT EXISTS (SELECT 1 FROM biller WHERE bAccNum = ?)";
        try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, billerFirstName);
            ps.setString(2, billerLastName);
            ps.setString(3, billerAccNumber);
            ps.setString(4, billerAccNumber);
            ps.executeUpdate();

            // If the biller was inserted, retrieve the generated ID
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);  // Return the generated biller ID
                }
            }
        }

        // If the biller already exists, retrieve the biller ID
        String getIdQuery = "SELECT billerId FROM biller WHERE bAccNum = ?";
        try (PreparedStatement ps = con.prepareStatement(getIdQuery)) {
            ps.setString(1, billerAccNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("billerId");  // Return the existing biller ID
                }
            }
        }

        throw new SQLException("Failed to insert or retrieve biller ID");
    }

}
