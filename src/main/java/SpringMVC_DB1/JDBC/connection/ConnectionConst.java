package SpringMVC_DB1.JDBC.connection;

import io.github.cdimascio.dotenv.Dotenv;

// 데이터 베이스 접속 필요 정보 상수로 정의
public abstract class ConnectionConst {

    // 환경 변수 로드용 객체
    private static final Dotenv dotenv = Dotenv.load();

    public static final String URL = String.format("jdbc:mysql://%s:%s/%s",
            dotenv.get("MYSQL_HOST"), dotenv.get("MYSQL_PORT"), dotenv.get("MYSQL_DATABASE"));
    public static final String USERNAME = dotenv.get("MYSQL_USER");
    public static final String PASSWORD = dotenv.get("MYSQL_PASSWORD");
}
