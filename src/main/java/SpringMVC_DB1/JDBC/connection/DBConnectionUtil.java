package SpringMVC_DB1.JDBC.connection;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", con, con.getClass());
            return con;
        } catch(SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
