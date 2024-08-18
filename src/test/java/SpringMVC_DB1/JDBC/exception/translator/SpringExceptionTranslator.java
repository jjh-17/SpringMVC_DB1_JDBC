package SpringMVC_DB1.JDBC.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
public class SpringExceptionTranslator {

    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    @DisplayName("SQL 예외 에러 코드")
    void sqlExceptionErrorCode() {
        String sql = "select bad grammar";

        try(Connection con = dataSource.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.executeQuery();
        } catch(SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(1054);
            
            log.error("errorCode = {}", errorCode);
            log.error("error", e);
        }
    }

    @Test
    @DisplayName("스프링 SQL 예외 변환기")
    void springExceptionTranslator() {
        String sql = "select bad grammar";

        try(Connection con = dataSource.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql)) {
            psmt.executeQuery();
        } catch(SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(1054);

            SQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = exTranslator.translate("select", sql, e);
            log.error("resultEx", resultEx);
            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }
}
