package persistence.sql.dml;

import jdbc.RowMapper;

import java.sql.Connection;

/**
 * 데이터베이스를 나타내는 인터페이스
 */
public interface Database {
    /**
     * 데이터베이스와의 연결을 반환한다.
     */
    Connection getConnection();

    /**
     * INSERT, UPDATE, DELETE 등의 쿼리를 실행한다.
     *
     * @param query 실행할 쿼리
     */
    Object executeUpdate(String query);

    /**
     * SELECT 쿼리를 실행하고 결과를 반환한다.
     *
     * @param query     실행할 쿼리
     * @param rowMapper ResultSet을 매핑하는 함수
     * @return 쿼리 실행 결과
     */
    <T> T executeQuery(String query, RowMapper<T> rowMapper);
}
