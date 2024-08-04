package SpringMVC_DB1.JDBC.connection;

import io.github.cdimascio.dotenv.Dotenv;

// 데이터 베이스 접속 필요 정보 상수로 정의
public abstract class ConnectionConst {

    private static final Dotenv dotenv = Dotenv.load();

    public static final String URL = dotenv.get("SPRING_DATASOURCE_URL");
    public static final String USERNAME = dotenv.get("SPRING_DATASOURCE_USERNAME");
    public static final String PASSWORD = dotenv.get("SPRING_DATASOURCE_PASSWORD");
}
