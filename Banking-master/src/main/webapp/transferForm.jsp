<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transfer Form</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h2>Transfer Funds</h2>

    <form action="TransferServlet" method="POST">
<%--        <div class="form-group">--%>
<%--            <label for="fromAccNumber">From Account Number</label>--%>
<%--            <input type="text" class="form-control" id="fromAccNumber" name="fromAccNumber" required>--%>
<%--        </div>--%>
        <div class="form-group">
            <label for="toAccNumber">To Account Number</label>
            <input type="text" class="form-control" id="toAccNumber" name="toAccNumber" required>
        </div>
        <div class="form-group">
            <label for="amount">Amount</label>
            <input type="number" class="form-control" id="amount" name="amount" required>
        </div>

        <!-- Biller's First Name -->
        <div class="form-group">
            <label for="billerFirstName">Biller's First Name</label>
            <input type="text" class="form-control" id="billerFirstName" name="billerFirstName" required>
        </div>

        <!-- Biller's Last Name -->
        <div class="form-group">
            <label for="billerLastName">Biller's Last Name</label>
            <input type="text" class="form-control" id="billerLastName" name="billerLastName" required>
        </div>

        <button type="submit" class="btn btn-primary">Transfer</button>
    </form>

    <%-- Display messages --%>
    <p class="text-danger">
        <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
    </p>
    <p class="text-success">
        <%= request.getAttribute("message") != null ? request.getAttribute("message") : "" %>
    </p>
</div>

</body>
</html>
