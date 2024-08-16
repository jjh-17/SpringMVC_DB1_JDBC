package SpringMVC_DB1.JDBC.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedTest {
    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw() {
        Service service = new Service();
        assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUncheckedException.class);
    }

    // 예외 처리 서비스
    static class Service {
        Repository repository = new Repository();
        
        // 필요에 따라 예외 처리
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                //예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }
        
        // 예외 잡지 않아도 상위로 자동 이동
        // throws 예외 선언 불필요
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUncheckedException(("ex"));
        }
    }

    // 언체크 예외
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }
}