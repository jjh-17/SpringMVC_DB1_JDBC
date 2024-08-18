package SpringMVC_DB1.JDBC.exception.translator;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.exception.MyDbException;
import SpringMVC_DB1.JDBC.exception.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

@SpringBootTest
public class ExTranslatorV1Test {
    private Repository repository;
    private Service service;

    @BeforeEach
    void init() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);

        repository.delete();
    }

    @Test
    @DisplayName("동일 ID 저장")
    void duplicateKeySaveTest() {
        service.create("myId");
        service.create("myId");
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        public void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("키 생성 완료 = {}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.error("키 중복, 복구 시도");
                String retryId = generateNewId(memberId);
                repository.save(new Member(retryId, 0));
                log.info("키 생성 완료 = {}", retryId);
            } catch (MyDbException e) {
                log.error("데이터 접근 계층 예외", e);
                throw e;
            } 
        }

        // 새키 생성 메서드
        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
    }

    // 리포지토리
    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, money) values(?, ?)";

            try(Connection con = dataSource.getConnection();
                PreparedStatement psmt = con.prepareStatement(sql)) {
                psmt.setString(1, member.getMemberId());
                psmt.setInt(2, member.getMoney());
                psmt.executeUpdate();

                return member;
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062)   throw new MyDuplicateKeyException(e);
                throw new MyDbException(e);
            }
        }

        public void delete() {
            String sql = "delete from member";

            try(Connection con = dataSource.getConnection();
                PreparedStatement psmt = con.prepareStatement(sql)) {
                psmt.executeUpdate();
            } catch (SQLException e) {
                throw new MyDbException(e);
            }
        }
    }
}
