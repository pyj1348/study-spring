package hello.hellospring.repository.study;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            // 같은 데이터 커넥션을 유지하기 위해 스프링 DataSource를 통한 connection 연결결
           conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, member.getName());

            pstmt.executeUpdate();
            rset = pstmt.getGeneratedKeys();

            if (rset.next()) {
                member.setId(rset.getLong(1));
            } else {
                throw new SQLException("조회된 회원이 없습니다.");
            }
            return member;
        } catch (Exception e) {
            throw new IllegalStateException();
        } finally {
            close(conn, pstmt, rset);
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            // 같은 데이터 커넥션을 유지하기 위해 스프링 DataSource를 통한 connection 연결결
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);

            rset = pstmt.executeQuery();

            if (rset.next()) {
                Member member = new Member();
                member.setId(rset.getLong("id"));
                member.setName(rset.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException();
        } finally {
            close(conn, pstmt, rset);
        }
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            // 같은 데이터 커넥션을 유지하기 위해 스프링 DataSource를 통한 connection 연결결
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, name);

            rset = pstmt.executeQuery();

            if (rset.next()) {
                Member member = new Member();
                member.setId(rset.getLong("id"));
                member.setName(rset.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException();
        } finally {
            close(conn, pstmt, rset);
        }

    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from member";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            // 같은 데이터 커넥션을 유지하기 위해 스프링 DataSource를 통한 connection 연결결
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            rset = pstmt.executeQuery();

            List<Member> members = new ArrayList<>();
            while (rset.next()) {
                Member member = new Member();
                member.setId(rset.getLong("id"));
                member.setName(rset.getString("name"));
                members.add(member);
            }

            return members;
        } catch (Exception e) {
            throw new IllegalStateException();
        } finally {
            close(conn, pstmt, rset);
        }}

    private Connection getConnection(){
        return DataSourceUtils.getConnection(dataSource);
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rset) {
        try {
            if(rset != null){
                rset.close();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }try {
            if(pstmt != null){
                pstmt.close();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }try {
            if(conn != null){
                close(conn);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void close(Connection conn) throws SQLException{

        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
