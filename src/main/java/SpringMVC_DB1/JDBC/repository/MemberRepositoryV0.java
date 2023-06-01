package SpringMVC_DB1.JDBC.repository;

import SpringMVC_DB1.JDBC.connection.DBConnectionUtil;
import SpringMVC_DB1.JDBC.domain.Member;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

//JDBC DriverManager 사용
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement preparedStatement = null;

        try{
            con = getConnection();

            //Statement의 자식, '?'을 통한 파아미터 바인딩 가능, SQL Injection 공격 예방
            preparedStatement = con.prepareStatement(sql);

            //sql의 첫번째, 두번째 ?에 값을 넣는다
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate(); //sql을 실제 DB로 전달. 영향을 받은 DB row의 수 반환

            return member;

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {

            close(con, preparedStatement, null);
        }
    }


    //Id를 이용한 탐색
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            con = getConnection();

            ps = con.prepareStatement(sql);
            ps.setString(1, memberId);

            //쿼리문 결과
            rs = ps.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));

                return member;
            } else{
                //rs에 데이터가 존재하지 않음
                throw new NoSuchElementException("memberId = " + memberId + " 탐색 실패");
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }finally {
            close(con, ps, rs);
        }
    }

    //정보 수정
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = getConnection();

            //Statement의 자식, '?'을 통한 파아미터 바인딩 가능, SQL Injection 공격 예방
            ps = con.prepareStatement(sql);

            //sql의 첫번째, 두번째 ?에 값을 넣는다
            ps.setInt(1, money);
            ps.setString(2, memberId);
            int size = ps.executeUpdate(); //sql을 실제 DB로 전달. 영향을 받은 DB row의 수 반환

            log.info("resultSize={}", size);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, ps, null);
        }
    }

    //정보 삭제
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = getConnection();

            ps = con.prepareStatement(sql);
            ps.setString(1, memberId);

            int size = ps.executeUpdate(); //sql을 실제 DB로 전달. 영향을 받은 DB row의 수 반환

            log.info("resultSize={}", size);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, ps, null);
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

    /*
    -외부 리소스를 사용하므로 역할이 끝난 preparedStatement, con 종료
    -사용 역순으로 진행할 것!

    [ResultSet]
    -select 쿼리의 결과가 순서대로 들어감
        ==> EX) "select A, B"면 A, B라는 이름으로 데이터가 저장됨
    -ResultSet 내부 커서를 이동시켜 데이터 조회. 최초의 커서는 데이터를 가리키고 있지 않음
     */
    private void close(Connection con, PreparedStatement ps, ResultSet set) {
        if (set != null) {
            try {
                set.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }
}
