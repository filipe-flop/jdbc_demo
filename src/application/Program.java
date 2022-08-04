package application;

import db.DB;
import db.DbException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Program {

    public static void main(String[] args) {

        //if you will work with dates
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Connection conn = null;

        //Example 1: execute a query
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();

            st = conn.createStatement();

            rs = st.executeQuery("SELECT * FROM YOUR_TABLE");

            while (rs.next()) {
                System.out.println(rs.getString("COLUMN1") + ", " + rs.getString("COLUMN2"));
            }
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
            DB.closeConnection();
        }

        //Example 2: execute an insert
        PreparedStatement ps = null;

        try {
            conn = DB.getConnection();

            conn.setAutoCommit(false);

            ps = conn.prepareStatement(
                    "INSERT INTO YOUR_TABLE "
                    + "(COLUMN1, COLUMN2, COLUMN3) "
                    + "VALUES "
                    + "(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS); //return the primary key generated after the insert (optional)

            /*
            insert more than one value for the same column at once
             ps = conn.prepareStatement(
                    "INSERT INTO YOUR_TABLE (COLUMN1) VALUES ('value1'),('value2')");
             */

            ps.setString(1,"value for column1"); //if the data type isn't a string, use another set (setInt, setBoolean, etc)
            ps.setString(2,"value for column2");
            ps.setDate(3,new java.sql.Date(sdf.parse("01/31/2022").getTime())); //supposing column3 is a date type

            int rowsAffected = ps.executeUpdate();
            conn.commit();

            //optional treatment
            if (rowsAffected > 0) {
                ResultSet rst = ps.getGeneratedKeys();
                while (rst.next()) {
                    int key = rs.getInt(1);
                    System.out.println("Succesfully inserted. Key = " + key);
                }
            }
            else {
                System.out.println("No rows updated.");
            }
        }
        catch (SQLException e) {
            try {
                conn.rollback();
                throw new DbException("Transaction rolled back. Caused by: " + e.getMessage());
            } catch (SQLException ex) {
                throw new DbException("Error trying to rollback. Caused by: " + ex.getMessage());
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            DB.closeStatement(ps);
            DB.closeConnection();
        }

        //Example 3: execute an update
        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(
                    "UPDATE YOUR_TABLE "
                            + "SET COLUMN2 = ? "
                            + "WHERE "
                            + "(COLUMN1 = ?)");

            ps.setDouble(1,1000.00); //supposing column2 is double
            ps.setInt(2,123); //supposing column1 is an integer (may be an id)

            ps.executeUpdate();
            conn.commit();

        }
        catch (SQLException e) {
            try {
                conn.rollback();
                throw new DbException("Transaction rolled back. Caused by: " + e.getMessage());
            } catch (SQLException ex) {
                throw new DbException("Error trying to rollback. Caused by: " + ex.getMessage());
            }
        }
        finally {
            DB.closeStatement(ps);
            DB.closeConnection();
        }

        /*
        To delete from database just replace the update/insert statement for a delete statement (using conn.prepareStatement)

        Example:
        ps = conn.prepareStatement(
                    "DELETE FROM YOUR_TABLE "
                    + "SET COLUMN2 = ? "
                    + "WHERE "
                    + "(COLUMN1 = ?)");
         */
    }

}
