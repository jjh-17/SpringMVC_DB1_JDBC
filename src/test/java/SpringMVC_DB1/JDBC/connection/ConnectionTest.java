package SpringMVC_DB1.JDBC.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    // DriverManager를 통한 커넥션 획득
    @Test
    public void driverManager() throws Exception {
        // given
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        //then
        showConnectionInfo(con1, con2);
    }

    // Driver Manager 테스트
    @Test
    public void dataSourceDriverManager() throws Exception {
        // DriverManagerDatasource: 항상 새로운 커넥션을 획득
        //given
        DriverManagerDataSource ds = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        Connection con1 = ds.getConnection();
        Connection con2 = ds.getConnection();

        //then
        showConnectionInfo(con1, con2);
    }

    // 커넥션 풀 테스트
    @Test
    public void dataSourceConnectionPoolTest() throws Exception {
        // given
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("test pool");

        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();

        //then
        showConnectionInfo(con1, con2);
        Thread.sleep(1000);  // 커넥션 생성 작엄은 별도의 스레드에서 작동 ==> 대기 시간을 주어 커넥션 생성 로그 확인 ==> 왜 안되지
    }

    // 커넥션 정보 출력
    private void showConnectionInfo(Connection con1, Connection con2) {
        log.info("con1={}, class={}", con1, con1.getClass());
        log.info("con2={}, class={}", con2, con2.getClass());
    }
}
