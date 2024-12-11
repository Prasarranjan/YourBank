package Controller;

import Bean.TransactionDetail;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/TransactionHistory")
public class TransactionHistoryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String custId = request.getParameter("custId");
        List<TransactionDetail> transactions = new ArrayList<>();

        String dbUrl = "jdbc:mysql://localhost:3306/your_database";
        String dbUser = "root";
        String dbPassword = "prasar123";

        String query = "SELECT ROW_NUMBER() OVER (ORDER BY t.transDate) AS slno, " +
                "CONCAT(b.bFname, ' ', b.bLname) AS billerName, " +
                "b.bAccNum, t.transNum, t.transMode, t.transDate, t.amount, t.transStatus " +
                "FROM biller b INNER JOIN transaction t ON b.billerId = t.billerId " +
                "WHERE t.custId = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(custId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TransactionDetail transaction = new TransactionDetail();
                    transaction.setSlno(rs.getInt("slno"));
                    transaction.setBillerName(rs.getString("billerName"));
                    transaction.setBAccNum(rs.getString("bAccNum"));
                    transaction.setTransNum(rs.getString("transNum"));
                    transaction.setTransMode(rs.getString("transMode"));
                    transaction.setTransDate(rs.getString("transDate"));
                    transaction.setAmount(rs.getDouble("amount"));
                    transaction.setTransStatus(rs.getString("transStatus"));
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("transactions", transactions);
        request.getRequestDispatcher("transactionHistory.jsp").forward(request, response);
    }
}
