package SpringMVC_DB1.JDBC;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class JdbcApplication {

	public static void main(String[] args) {
		setSysArgs();
		SpringApplication.run(JdbcApplication.class, args);
	}

	private static void setSysArgs() {
		Dotenv dotenv = Dotenv.configure().load();

		System.setProperty("MYSQL_HOST", dotenv.get("MYSQL_HOST"));
		System.setProperty("MYSQL_PORT", dotenv.get("MYSQL_PORT"));
		System.setProperty("MYSQL_DATABASE", dotenv.get("MYSQL_DATABASE"));
		System.setProperty("MYSQL_USER", dotenv.get("MYSQL_USER"));
		System.setProperty("MYSQL_PASSWORD", dotenv.get("MYSQL_PASSWORD"));
		System.setProperty("MYSQL_ROOT_PASSWORD", dotenv.get("MYSQL_ROOT_PASSWORD"));

		System.setProperty("SPRING_DATASOURCE_HOST", dotenv.get("SPRING_DATASOURCE_HOST"));
		System.setProperty("SPRING_DATASOURCE_PORT", dotenv.get("SPRING_DATASOURCE_PORT"));
		System.setProperty("SPRING_DATASOURCE_URL",
				String.format("jdbc:mysql://%s:%s/%s",
						dotenv.get("SPRING_DATASOURCE_HOST"),
						dotenv.get("SPRING_DATASOURCE_PORT"),
						dotenv.get("MYSQL_DATABASE")));
		System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
	}

}
