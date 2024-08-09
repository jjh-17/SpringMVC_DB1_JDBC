package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV3;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
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
class MemberServiceV3_4Test {

    static {
        setSysArgs();
    }

    //테스트 내 적용할 설정 정보
    @TestConfiguration
    static class TestConfig {
        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource);
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }

//    //테스트 내 적용할 설정 정보
//    @TestConfiguration
//    @RequiredArgsConstructor
//    static class TestConfig2 {
//        private final DataSource dataSource;
//
//        @Bean
//        MemberRepositoryV3 memberRepositoryV3() {
//            return new MemberRepositoryV3(dataSource);
//        }
//
//        @Bean
//        MemberServiceV3_3 memberServiceV3_3() {
//            return new MemberServiceV3_3(memberRepositoryV3());
//        }
//    }

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 2000;

    @Autowired private MemberRepositoryV3 memberRepositoryV3;
    @Autowired private MemberServiceV3_3 memberServiceV3_3;

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV3.delete(MEMBER_A);
        memberRepositoryV3.delete(MEMBER_B);
        memberRepositoryV3.delete(MEMBER_EX);
    }

    //정보 확인
    @Test
    @DisplayName("정보 확인")
    public void AppCheck() throws Exception {
        //given

        //when

        //then
        log.info("memberService class={}", memberServiceV3_3.getClass());
        log.info("memberRepository class={}", memberRepositoryV3.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberServiceV3_3)).isTrue();
        Assertions.assertThat(AopUtils.isAopProxy(memberRepositoryV3)).isFalse();
    }


    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 20000);
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberB);

        //when
        memberServiceV3_3.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), MONEY);

        //then
        Member findA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findB = memberRepositoryV3.findById(memberB.getMemberId());
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
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberEX);

        //when - 아이디가 "ex"인 멤버의 잔액 변경 실패
        Assertions.assertThatThrownBy(
                        () -> memberServiceV3_3.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), MONEY))
                .isInstanceOf(IllegalStateException.class);
        //then
        Member findA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findEX = memberRepositoryV3.findById(memberEX.getMemberId());
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