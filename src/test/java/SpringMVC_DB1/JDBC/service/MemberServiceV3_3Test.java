package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV3;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

//@Transactional AOP 테스트
@Slf4j
@SpringBootTest //@Transactinal과 같은 스프링 AOP를 테스트에 적용하려면 선언 필요
class MemberServiceV3_3Test {

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 2000;

    @Autowired
    private MemberRepositoryV3 memberRepositoryV3;
    @Autowired
    private MemberServiceV3_3 memberServiceV3_3;

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV3.delete(MEMBER_A);
        memberRepositoryV3.delete(MEMBER_B);
        memberRepositoryV3.delete(MEMBER_EX);
    }

    //테스트 내 적용할 설정 정보
    @TestConfiguration
    static class TestConfig{
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
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

}