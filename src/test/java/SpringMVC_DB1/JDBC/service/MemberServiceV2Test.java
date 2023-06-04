package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

//커넥션을 파라미터로 전달하는 계좌이체 테스트
@Slf4j
class MemberServiceV2Test {

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 2000;

    private MemberRepositoryV2 memberRepositoryV2;
    private MemberServiceV2 memberServiceV2;

    @BeforeEach
    void before() {
        //데이터 소스 생성
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV2 = new MemberRepositoryV2(dataSource);
        memberServiceV2 = new MemberServiceV2(dataSource, memberRepositoryV2);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV2.delete(MEMBER_A);
        memberRepositoryV2.delete(MEMBER_B);
        memberRepositoryV2.delete(MEMBER_EX);
    }


    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 20000);
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberB);

        //when
        memberServiceV2.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), MONEY);

        //then
        Member findA = memberRepositoryV2.findById(memberA.getMemberId());
        Member findB = memberRepositoryV2.findById(memberB.getMemberId());
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
        memberRepositoryV2.save(memberA);
        memberRepositoryV2.save(memberEX);

        //when - 아이디가 "ex"인 멤버의 잔액 변경 실패
        Assertions.assertThatThrownBy(
                        () -> memberServiceV2.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), MONEY))
                .isInstanceOf(IllegalStateException.class);
        //then
        Member findA = memberRepositoryV2.findById(memberA.getMemberId());
        Member findEX = memberRepositoryV2.findById(memberEX.getMemberId());
        Assertions.assertThat(findA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(findEX.getMoney()).isEqualTo(20000);

    }

}