package SpringMVC_DB1.JDBC.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MyDbException extends RuntimeException {
    public MyDbException(String msg) {
        super(msg);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }

    public MyDbException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
