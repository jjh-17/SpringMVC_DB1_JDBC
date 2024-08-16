package SpringMVC_DB1.JDBC.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedTest {

    // 예외 처리 테스트
    @Test
    void checkedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    // 예외 던짐 테스트
    @Test
    void checkedThrow() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyCheckedException.class);
    }

    static class Service {
        Repository repository = new Repository();

        // 예외 처리 코드
        public void callCatch() {
            try {
                repository.call();
            } catch(MyCheckedException e) {
                log.error("체크 예외 처리, message={}", e.getMessage(), e);
            }
        }

        // 예외 던짐 코드
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    // Exception 던짐 클래스
    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }

    // Exception 처리 => 체크 예외
    static class MyCheckedException extends Exception {
        public MyCheckedException(String msg) {
            super(msg);
        }
    }
}
