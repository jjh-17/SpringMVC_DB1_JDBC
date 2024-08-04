package SpringMVC_DB1.JDBC.dotenv;


import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

class DotenvTest {

    @Test
    public void testEnvVars() {
        Dotenv dotenv = Dotenv.configure().load();

        System.out.println(dotenv.get("MYSQL_HOST"));
        System.out.println(dotenv.get("MYSQL_PORT"));
        System.out.println(dotenv.get("MYSQL_DATABASE"));
        System.out.println(dotenv.get("MYSQL_USER"));
        System.out.println(dotenv.get("MYSQL_PASSWORD"));
        System.out.println(dotenv.get("MYSQL_ROOT_PASSWORD"));
    }

}
