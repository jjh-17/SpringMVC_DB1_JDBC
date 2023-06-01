package SpringMVC_DB1.JDBC.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.Connection;

@Slf4j
class JDBConnectionUtilTest {

    @Test
    public void connection() throws Exception {
        //given
        Connection connection = DBConnectionUtil.getConnection();

        //when

        //then
        Assertions.assertThat(connection).isNotNull();
    }

}