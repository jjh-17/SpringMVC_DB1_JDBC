package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

//트랜잭션 매니저 테스트
@Slf4j
class MemberServiceV3_1Test {

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 2000;

    private MemberRepositoryV3 memberRepositoryV3;
    private MemberServiceV3_1 memberServiceV3_1;

    @BeforeEach
    void before() {
        //데이터 소스 생성
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV3 = new MemberRepositoryV3(dataSource);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberServiceV3_1 = new MemberServiceV3_1(transactionManager, memberRepositoryV3);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV3.delete(MEMBER_A);
        memberRepositoryV3.delete(MEMBER_B);
        memberRepositoryV3.delete(MEMBER_EX);
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
        memberServiceV3_1.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), MONEY);

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
                        () -> memberServiceV3_1.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), MONEY))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findEX = memberRepositoryV3.findById(memberEX.getMemberId());
        Assertions.assertThat(findA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findEX.getMoney()).isEqualTo(20000);
    }
}