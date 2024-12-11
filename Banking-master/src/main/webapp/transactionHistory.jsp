<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Get the customer ID (user ID) from the session
    Integer userId = (Integer) session.getAttribute("userid");
    if (userId == null) {
        response.sendRedirect("login.jsp"); // Redirect to login if user is not logged in
        return;
    }

    // Database connection details
    String dbUrl = "jdbc:mysql://localhost:3306/banking";
    String dbUser = "root";
    String dbPassword = "prasar123";

    // Query to fetch transactions
    String query = "SELECT ROW_NUMBER() OVER (ORDER BY t.transDate) AS slno, " +
            "CONCAT(b.bFname, ' ', b.bLname) AS billerName, " +
            "b.bAccNum, t.transNum, t.transMode, t.transDate, t.amount, t.transStatus " +
            "FROM biller b INNER JOIN transaction t ON b.billerId = t.billerId " +
            "WHERE t.custId = ?";

    // Prepare list to hold transaction details
    List<Map<String, Object>> transactions = new ArrayList<>();

    try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, userId); // Set the user ID in the query

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("slno", rs.getInt("slno"));
                transaction.put("billerName", rs.getString("billerName"));
                transaction.put("bAccNum", rs.getString("bAccNum"));
                transaction.put("transNum", rs.getString("transNum"));
                transaction.put("transMode", rs.getString("transMode"));
                transaction.put("transDate", rs.getString("transDate"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("transStatus", rs.getString("transStatus"));
                transactions.add(transaction);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
%>
<html>
<head>
    <title>Trasaction History</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .table {
            background-color: #ffffff;
        }
        .table thead {
            background-color: #007bff;
            color: #ffffff;
        }
        .status-success {
            color: green;
            font-size: 1.5em;
            font-weight: bold;
        }
        .status-failure {
            color: red;
            font-size: 1.5em;
            font-weight: bold;
        }
    </style>
</head>
<body>

<div class="container mt-4">
    <h2 class="text-primary">Transaction History</h2>

    <table class="table table-bordered">
        <thead>
        <tr>
            <th>Sl. No</th>
            <th>Biller Name</th>
            <th>Biller Account Number</th>
            <th>Transaction Number</th>
            <th>Transaction Mode</th>
            <th>Transaction Date</th>
            <th>Amount</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (!transactions.isEmpty()) {
                for (Map<String, Object> transaction : transactions) {
                    String transStatus = (String) transaction.get("transStatus");
                    boolean isSuccessful = "Success".equalsIgnoreCase(transStatus);
        %>
        <tr>
            <td><%= transaction.get("slno") %></td>
            <td><%= transaction.get("billerName") %></td>
            <td><%= transaction.get("bAccNum") %></td>
            <td><%= transaction.get("transNum") %></td>
            <td><%= transaction.get("transMode") %></td>
            <td><%= transaction.get("transDate") %></td>
            <td><%= transaction.get("amount") %></td>
            <td class="<%= isSuccessful ? "status-success" : "status-failure" %>">
                <%= isSuccessful ? "✔" : "✔" %>
            </td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="8" class="text-center">No transactions available.</td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
