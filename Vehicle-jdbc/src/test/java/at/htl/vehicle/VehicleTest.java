package at.htl.vehicle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class VehicleTest {
    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    public static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db;create=true";
    public static final String USER = "app";
    public static final String PASSWORD = "app";
    public static Connection conn;

    @BeforeClass
    public static void initJdbc(){
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Verbindung zur Datenbank nicht möglich\n" + e.getMessage() +"\n");
            System.exit(1);
        }


        try {
            Statement stmt = conn.createStatement();

            String sql = "Create Table vehicle(" +
                    " id INT constraint vehicle_pk PRIMARY KEY" +
                    " GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    " brand VARCHAR(255) NOT NULL," +
                    " type VARCHAR(255) NOT NULL" +
                    " )";

            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Tabelle VEHICLE erstellt");
    }

    @AfterClass
    public static void teardownJdbc(){
        try {
            conn.createStatement().execute("Drop table VEHICLE");
            System.out.println("Tabelle VEHICLE gelöscht");
        } catch (SQLException e) {
            System.err.println("Tabelle VEHICLE konnte nicht gelöscht werden\n"+e.getMessage());
        }

        try {
            if (conn != null && !conn.isClosed()){

                conn.close();
                System.out.println("Good bye");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dml(){
        int countInserts = 0;
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO vehicle (brand,type) values ('Opel', 'Commodore')";
            countInserts +=stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (brand,type) values ('Opel', 'Kapitän')";
            countInserts +=stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (brand,type) values ('Opel', 'Kadett')";
            countInserts +=stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertThat(countInserts, is(3));
        System.out.println("Daten in Tabelle VEHICLE eingefügt");

        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT ID, BRAND, TYPE from VEHICLE");
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            assertThat(rs.getString("BRAND"), is("Opel"));
            assertThat(rs.getString("TYPE"), is("Commodore"));
            rs.next();
            assertThat(rs.getString("BRAND"), is("Opel"));;
            assertThat(rs.getString("TYPE"), is("Kapitän"));
            rs.next();
            assertThat(rs.getString("BRAND"), is("Opel"));;
            assertThat(rs.getString("TYPE"), is("Kadett"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}