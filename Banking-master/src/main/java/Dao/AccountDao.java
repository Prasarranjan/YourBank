package Dao;

import Bean.Account;
import Bean.AccountDetails;
import Bean.Branch;
import Bean.userDetails;
import Util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    public static int createNewAccount( Long accNumber,int custId,int accTypeId,double balance ,int branchId) {
        int status=0;
        try {
            Connection con= DbConnection.getConnection();
            String query="INSERT INTO account(accNumber,custId,accTypeId ,balance,branchId,openingDate )VALUES(?,?,?,?,?,now())";
            PreparedStatement ps=con.prepareStatement(query);
            //accNumber,custId,accTypeId,balance,branchId
            ps.setLong(1,accNumber);
            ps.setInt(2,custId);
            ps.setInt(3,accTypeId);
            ps.setDouble(4,balance);
            ps.setInt(5,branchId);
            status=ps.executeUpdate();
            con.close();
        }  catch (SQLException e) {e.printStackTrace();}
        return status;
    }

    public List<AccountDetails> viewAccounts() {
        ArrayList<AccountDetails> bankList=new ArrayList<AccountDetails>();//Creating Arraylist
        try {
            Connection con = DbConnection.getConnection();
            String sql="SELECT \n" +
                    "    c.custImg AS customer_image, \n" +
                    "    CONCAT(c.custFname, ' ', c.custLname) AS customer_name, \n" +
                    "    b.branchName AS branch_name, \n" +
                    "    at.accTypeName AS account_type_name, \n" +
                    "    a.accNumber AS accNumber, \n" +
                    "    a.openingDate AS openingDate, \n" +
                    "    a.balance AS balance, \n" +
                    "    md.latitude AS latitude, \n" +
                    "    md.longitude AS longitude,\n" +
                    "    a.acctStatus AS account_status  -- Include isActive from the account table\n" +
                    "FROM account a\n" +
                    "JOIN customer c ON a.custId = c.custId\n" +
                    "JOIN branch b ON a.branchId = b.branchId\n" +
                    "JOIN mobiledevice md ON c.deviceId = md.deviceId\n" +
                    "JOIN accounttype at ON a.accTypeId = at.accTypeId\n" ;
            PreparedStatement ps=con.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
                AccountDetails br = new AccountDetails();
                br.setCustomerImage(rs.getString(1));
                br.setCustomerName(rs.getString(2));
                br.setBranchName(rs.getString(3));
                br.setAccountTypeName(rs.getString(4));
                br.setAccountNumber(rs.getString(5));
                br.setOpeningDate(rs.getString(6));
                br.setBalance(rs.getDouble(7));
                br.setLatitude(rs.getDouble(8));
                br.setLongitude(rs.getDouble(9));
                br.setIsActive(rs.getBoolean(10));
                bankList.add(br);
            }
            con.close();
        }catch(Exception ex) {ex.printStackTrace();}
        return bankList ;
    }

    public List<userDetails> viewAccountsDetails(int id) {
        ArrayList<userDetails> bankList=new ArrayList<userDetails>();//Creating Arraylist
        try {
            Connection con = DbConnection.getConnection();
            String sql="SELECT c.custFname,c.custLname,b.bankName,br.branchName,ac.balance,c.custAddress,c.custEmail,c.custPhone,c.custDOB,at.accTypeName,ac.accNumber,ac.openingDate FROM customer c INNER JOIN account ac ON c.custId = ac.custId INNER JOIN branch br ON ac.branchId = br.branchId INNER JOIN Bank b ON br.bankId = b.bankId INNER JOIN accounttype at ON ac.accTypeId = at.accTypeId WHERE c.custId =?;" ;
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
                userDetails br = new userDetails();
                br.setCustFname(rs.getString(1));
                br.setCustLname(rs.getString(2));
                br.setBankName(rs.getString(3));
                br.setBranchName(rs.getString(4));
                br.setBalance(rs.getDouble(5));
                br.setCustAddress(rs.getString(6));
                br.setCustEmail(rs.getString(7));
                br.setCustPhone(rs.getString(8));
                br.setCustDOB(rs.getString(9));
                br.setAccTypeName(rs.getString(10));
                br.setAcNumber(rs.getString(11));
                br.setOpeningDate(rs.getString(12));
                bankList.add(br);

            }
            con.close();
        }catch(Exception ex) {ex.printStackTrace();}
        return bankList ;
    }
}

//SELECT c.custFname,c.custLname,b.bankName,br.branchName,br.Ifsccode,ac.balance,c.custAddress,c.custEmail,c.custPhone,c.custDOB,at.accTypeName,ac.accNumber,ac.openingDate FROM customer c INNER JOIN account ac ON c.custId = ac.custId INNER JOIN branch br ON ac.branchId = br.branchId INNER JOIN Bank b ON br.bankId = b.bankId INNER JOIN accounttype at ON ac.accTypeId = at.accTypeId WHERE c.custId =52;