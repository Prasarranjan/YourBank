package Controller;

import Dao.TransferDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/TransferServlet")
public class TransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get user input
        String billerFirstName = request.getParameter("billerFirstName");
        String billerLastName = request.getParameter("billerLastName");
        HttpSession session = request.getSession();
        String fromAccNumber=session.getAttribute("accnumber").toString();
        String toAccNumber = request.getParameter("toAccNumber");
        double amount = Double.parseDouble(request.getParameter("amount"));

        // Get customer ID from session
        int custId = (int) session.getAttribute("userid");

        // Initialize DAO
        TransferDao transactionDAO = new TransferDao();

        // Process the transaction
        try {
            // Process the transaction in DAO, no need for accId in the session anymore
            boolean success = transactionDAO.processTransaction(fromAccNumber, toAccNumber, amount, custId,billerFirstName,billerLastName);
            if (success) {
                request.setAttribute("message", "Transaction successful.");
            } else {
                request.setAttribute("error", "Transaction failed. Please check the account details and balance.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred. Please try again.");
        }

        // Forward to the same page to display messages
        RequestDispatcher dispatcher = request.getRequestDispatcher("transferForm.jsp");
        dispatcher.forward(request, response);
    }
}
