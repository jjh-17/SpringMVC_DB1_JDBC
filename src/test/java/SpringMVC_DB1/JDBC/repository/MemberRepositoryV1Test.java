package SpringMVC_DB1.JDBC.repository;

import SpringMVC_DB1.JDBC.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import java.util.NoSuchElementException;
import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;


@Slf4j
class MemberRepositoryV1Test {

    private MemberRepositoryV1 repositoryV1;

    @BeforeEach
    void beforeEach() {
        //기본 DriverManager - 항상 새로운 커넥션 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repositoryV1 = new MemberRepositoryV1(dataSource);
    }

    @Test
    public void crud() throws Exception {
        //save
        Member member = new Member("memberV1", 10000);
        repositoryV1.save(member);

        //findById
        Member findMember = repositoryV1.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        Assertions.assertThat(member).isEqualTo(findMember);

        //update
        repositoryV1.update(member.getMemberId(), 20000);
        Member updateMember = repositoryV1.findById(member.getMemberId());
        Assertions.assertThat(updateMember.getMoney()).isEqualTo(20000);
        Assertions.assertThat(updateMember.getMemberId()).isEqualTo(member.getMemberId());

        //delete
        repositoryV1.delete(member.getMemberId());
        Assertions.assertThatThrownBy(() -> repositoryV1.findById(member.getMemberId()))
                        .isInstanceOf(NoSuchElementException.class);

        Thread.sleep(1000);
    }

}