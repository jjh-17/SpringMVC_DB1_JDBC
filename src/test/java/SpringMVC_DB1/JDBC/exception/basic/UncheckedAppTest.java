package SpringMVC_DB1.JDBC.exception.basic;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedAppTest {

    @Test
    @DisplayName("언체크 테스트")
    void UncheckedTest() {
        // given
        Controller controller = new Controller();

        // then
        assertThatThrownBy(controller::request)
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("예외 출력 테스트")
    void PrintEX() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch(Exception e) {
            log.error("ex", e);
        }
    }

    // 예외 최종 처리 controller
    static class Controller {
        Service service = new Service();

        void request() {
            service.logic();
        }
    }

    // SQL, 네트워크 에러 유발 메서드 호출 Service
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        void logic() {
            repository.call();
            networkClient.call();
        }
    }

    // SQL 체크 예외 감지 => 임의 SQL 언체크 예외 던짐
    static class Repository {
        void call() {
            try {
                runSQL();
            } catch(SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("ex-sql");
        }
    }

    // 런타임 네트워크 예외 감지 및 throw
    static class NetworkClient {
        public void call() throws RuntimeConnectException {
            throw new RuntimeConnectException("ex-connect");
        }
    }

    // 런타임 네트워크 예외
    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String msg) {
            super(msg);
        }
    }

    //
    @NoArgsConstructor
    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
