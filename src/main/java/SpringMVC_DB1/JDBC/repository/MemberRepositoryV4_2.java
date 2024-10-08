package SpringMVC_DB1.JDBC.repository;

import SpringMVC_DB1.JDBC.domain.Member;
import SpringMVC_DB1.JDBC.exception.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement psmtmt = null;

        try {
            con = getConnection();

            psmtmt = con.prepareStatement(sql);
            psmtmt.setString(1, member.getMemberId());
            psmtmt.setInt(2, member.getMoney());
            psmtmt.executeUpdate(); //sql을 실제 DB로 전달. 영향을 받은 DB row의 수 반환

            return member;
        } catch (SQLException e) {
            throw Objects.requireNonNull(exTranslator.translate("save", sql, e));
        } finally {
            close(con, psmtmt, null);
        }
    }


    //파라미터로 Connection을 받을 필요 없어짐
    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();

            psmt = con.prepareStatement(sql);
            psmt.setString(1, memberId);

            rs = psmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));

                return member;
            } else{
                throw new NoSuchElementException("memberId = " + memberId + " 탐색 실패");
            }
        } catch (SQLException e) {
            throw new MyDbException(e);
        }finally {
            close(con, psmt, rs);
        }
    }

    //파라미터로 Connection을 받을 필요 없어짐
    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement psmt = null;

        try{
            con = getConnection();

            psmt = con.prepareStatement(sql);
            psmt.setInt(1, money);
            psmt.setString(2, memberId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw Objects.requireNonNull(exTranslator.translate("update", sql, e));
        } finally {
            close(con, psmt, null);
        }
    }

    //정보 삭제
    @Override
    public void delete(String memberId) {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement psmt = null;

        try{
            con = getConnection();

            psmt = con.prepareStatement(sql);
            psmt.setString(1, memberId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw Objects.requireNonNull(exTranslator.translate("delete", sql, e));
        } finally {
            close(con, psmt, null);
        }
    }

    //트랜잭션 동기화 사용을 위한 DataSourceUtils
    private Connection getConnection() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("connection={}, class={}", connection, connection.getClass());
        return connection;
    }

    //트랜잭션 동기화 사용을 위한 DataSourceUtils
    private void close(Connection con, PreparedStatement psmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(psmt);
        DataSourceUtils.releaseConnection(con, dataSource); // 연결 종료, 커넥션 유지
    }
}
