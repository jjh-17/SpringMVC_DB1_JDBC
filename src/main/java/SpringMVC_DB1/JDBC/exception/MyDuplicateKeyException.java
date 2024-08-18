package SpringMVC_DB1.JDBC.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MyDuplicateKeyException extends MyDbException {
    public MyDuplicateKeyException(String msg) {
        super(msg);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }

    public MyDuplicateKeyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
