package Dao;

import Bean.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Method to get transaction history for a specific biller
    public List<Transaction> getTransactionHistory(int billerId) {
        List<Transaction> transactionList = new ArrayList<>();
        String query = "SELECT tId, transNum, transType, transMode, transDate, amount, transStatus, custId, accId, billerId " +
                "FROM transactions WHERE billerId = ? ORDER BY transDate DESC";

        try (Connection conn = Util.DbConnection.getConnection(); // Assuming you have a utility class for DB connection
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, billerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTId(rs.getInt("tId"));
                transaction.setTransNum(rs.getString("transNum"));
                transaction.setTransType(rs.getString("transType"));
                transaction.setTransMode(rs.getString("transMode"));
                transaction.setTransDate(rs.getDate("transDate"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setTransStatus(rs.getString("transStatus"));
                transaction.setCustId(rs.getInt("custId"));
                transaction.setAccId(rs.getInt("accId"));
                transaction.setBillerId(rs.getInt("billerId"));
                transactionList.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle exception
        }
        return transactionList;
    }
}

