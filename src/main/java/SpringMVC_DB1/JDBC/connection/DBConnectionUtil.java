package SpringMVC_DB1.JDBC.connection;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {
        try{
            // DB 연결 시도
            log.info(URL);
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());

            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
