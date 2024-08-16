package SpringMVC_DB1.JDBC.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

@Slf4j
public class CheckedAppTest {

    @Test
    void checkedTest() {
        // given
        Controller controller = new Controller();

        // then
        Assertions.assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }

    // 예외 최종 처리 controller
    static class Controller {
        Service service = new Service();

        void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    // SQL, 네트워크 유발 메서드 호출 Service
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }

    // SQL 에러
    static class Repository {
        void call() throws SQLException {
            throw new SQLException("ex-sql");
        }
    }

    // 네크워크 에러
    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("ex-connect");
        }
    }
}
