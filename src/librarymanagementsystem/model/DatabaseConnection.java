package librarymanagementsystem.model;

/**
 *
 * @author Bright
 */
import java.sql.*;
import org.sqlite.SQLiteConfig;

public class DatabaseConnection {

    public static Connection Connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            SQLiteConfig configuration = new SQLiteConfig();
            configuration.enforceForeignKeys(true);
            configuration.setBusyTimeout(String.valueOf(900000));
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Library.db", configuration.toProperties());
            return conn;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e);
            return null;
        }
    }

    public static void checkTable(String tableName,String query) {
        Statement st = null;
        Connection conn = null;
        try {
            conn = Connect();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, tableName, null);
            if (rs.next()) {
            } else {
                st = conn.createStatement();
                st.execute(query);
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
}
