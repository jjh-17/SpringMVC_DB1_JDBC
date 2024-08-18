package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepository;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV3;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV4_1;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@SpringBootTest
class MemberServiceV4Test {

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 2000;

    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberServiceV4 memberService;

    static {
        setSysArgs();
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    //테스트 내 적용할 설정 정보
    @TestConfiguration
    static class TestConfig {
        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepository() {
            return new MemberRepositoryV4_1(dataSource);
        }

        @Bean
        MemberServiceV4 memberServiceV4() {
            return new MemberServiceV4(memberRepository());
        }
    }

    //정보 확인
    @Test
    @DisplayName("정보 확인")
    public void AppCheck() throws Exception {
        //given

        //when

        //then
        log.info("memberService class={}", memberService.getClass());
        log.info("memberRepository class={}", memberRepository.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberService)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
    }


    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 20000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), MONEY);

        //then
        Member findA = memberRepository.findById(memberA.getMemberId());
        Member findB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(findA.getMoney()).isEqualTo(10000 - MONEY);
        Assertions.assertThat(findB.getMoney()).isEqualTo(20000 + MONEY);

    }

    //롤백이 진행되어 둘다 변함이 없음
    @Test
    @DisplayName("비정상 이체")
    public void accountTransferFail() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEX = new Member(MEMBER_EX, 20000);
        memberRepository.save(memberA);
        memberRepository.save(memberEX);

        //when - 아이디가 "ex"인 멤버의 잔액 변경 실패
        Assertions.assertThatThrownBy(
                        () -> memberService.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), MONEY))
                .isInstanceOf(IllegalStateException.class);
        //then
        Member findA = memberRepository.findById(memberA.getMemberId());
        Member findEX = memberRepository.findById(memberEX.getMemberId());
        Assertions.assertThat(findA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findEX.getMoney()).isEqualTo(20000);
    }

    private static void setSysArgs() {
        Dotenv dotenv = Dotenv.configure().load();

        System.setProperty("MYSQL_HOST", dotenv.get("MYSQL_HOST"));
        System.setProperty("MYSQL_PORT", dotenv.get("MYSQL_PORT"));
        System.setProperty("MYSQL_DATABASE", dotenv.get("MYSQL_DATABASE"));
        System.setProperty("MYSQL_USER", dotenv.get("MYSQL_USER"));
        System.setProperty("MYSQL_PASSWORD", dotenv.get("MYSQL_PASSWORD"));
        System.setProperty("MYSQL_ROOT_PASSWORD", dotenv.get("MYSQL_ROOT_PASSWORD"));

        System.setProperty("SPRING_DATASOURCE_HOST", dotenv.get("MYSQL_HOST"));
        System.setProperty("SPRING_DATASOURCE_PORT", dotenv.get("MYSQL_PORT"));
        System.setProperty("SPRING_DATASOURCE_URL",
                String.format("jdbc:mysql://%s:%s/%s",
                        dotenv.get("MYSQL_HOST"),
                        dotenv.get("MYSQL_PORT"),
                        dotenv.get("MYSQL_DATABASE")));
    }
}