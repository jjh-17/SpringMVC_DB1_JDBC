package SpringMVC_DB1.JDBC.repository;

import SpringMVC_DB1.JDBC.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

//트랜잭션 매니저 사용
@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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


    //파라미터로 Connection을 받을 필요 없어짐
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

    //파라미터로 Connection을 받을 필요 없어짐
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

    //트랜잭션 동기화 사용을 위한 DataSourceUtils
    private Connection getConnection() throws SQLException {
        //트랜잭션 동기화 매니저가 관리하는 커넥션이 있으면 해당 커넥션 반환, 없다면 새로운 커넥션 생성 후 반환
        //트랜잭션 시작
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("connection={}, class={}", connection, connection.getClass());
        return connection;
    }


    //트랜잭션 동기화 사용을 위한 DataSourceUtils
    private void close(Connection con, PreparedStatement ps, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(ps);

        //커넥션은 트랜잭션 종료 시점까지는 살아있어야함 ==> 연결만 끊을 뿐 커넥션을 닫지 않고 유지한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }
}
