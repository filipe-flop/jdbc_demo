package application;

import db.DB;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Program {

    public static void main(String[] args) {

        //if you will work with dates
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Connection conn = null;

        //execute a query
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
            e.printStackTrace();
        }
        finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
            DB.closeConnection();
        }

        //execute an insert
        PreparedStatement ps = null;

        try {
            conn = DB.getConnection();

            ps = conn.prepareStatement(
                    "INSERT INTO YOUR_TABLE "
                    + "(COLUMN1, COLUMN2, COLUMN3) "
                    + "VALUES "
                    + "(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS); //return the primary key generated after the insert

            /*
            insert more than one value for the same column at once
             ps = conn.prepareStatement(
                    "INSERT INTO YOUR_TABLE (COLUMN1) VALUES ('value1'),('value2')");
             */

            ps.setString(1,"value for column1"); //if the data type isn't a string, use another set (setInt, setBoolean, etc)
            ps.setString(2,"value for column2");
            ps.setDate(3,new java.sql.Date(sdf.parse("01/31/2022").getTime())); //supposing column3 is a date type

            int rowsAffected = ps.executeUpdate();

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
            e.printStackTrace();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            DB.closeStatement(ps);
            DB.closeConnection();
        }

    }

}
