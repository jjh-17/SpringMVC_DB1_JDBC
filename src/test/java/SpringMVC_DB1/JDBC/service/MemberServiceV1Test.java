package SpringMVC_DB1.JDBC.service;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.repository.MemberRepositoryV1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import java.sql.SQLException;
import static SpringMVC_DB1.JDBC.connection.ConnectionConst.*;

@Slf4j
class MemberServiceV1Test {

    private static final String MEMBER_A = "memberA";
    private static final String MEMBER_B = "memberB";
    private static final String MEMBER_EX = "ex";
    private static final int MONEY = 2000;

    private MemberRepositoryV1 memberRepositoryV1;
    private MemberServiceV1 memberServiceV1;

    @BeforeEach
    void before() {
        //데이터 소스 생성
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepositoryV1 = new MemberRepositoryV1(dataSource);
        memberServiceV1 = new MemberServiceV1(memberRepositoryV1);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV1.delete(MEMBER_A);
        memberRepositoryV1.delete(MEMBER_B);
        memberRepositoryV1.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    public void accountTransfer() throws Exception {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 20000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberB);

        //when
        memberServiceV1.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), MONEY);

        //then
        Member findA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findB = memberRepositoryV1.findById(memberB.getMemberId());
        Assertions.assertThat(findA.getMoney()).isEqualTo(10000 - MONEY);
        Assertions.assertThat(findB.getMoney()).isEqualTo(20000 + MONEY);

    }

    @Test
    @DisplayName("비정상 이체")
    public void accountTransferFail() throws SQLException {
        //given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEX = new Member(MEMBER_EX, 20000);
        memberRepositoryV1.save(memberA);
        memberRepositoryV1.save(memberEX);

        //when - 아이디가 "ex"인 멤버의 잔액 변경 실패
        Assertions.assertThatThrownBy(
                        () -> memberServiceV1.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), MONEY))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findA = memberRepositoryV1.findById(memberA.getMemberId());
        Member findEX = memberRepositoryV1.findById(memberEX.getMemberId());
        Assertions.assertThat(findA.getMoney()).isEqualTo(10000 - MONEY);
        Assertions.assertThat(findEX.getMoney()).isEqualTo(20000);
    }

}