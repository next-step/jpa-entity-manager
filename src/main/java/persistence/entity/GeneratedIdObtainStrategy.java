package persistence.entity;

import jdbc.RowMapper;

public interface GeneratedIdObtainStrategy {
    String getQueryString();
    RowMapper<Object> getRowMapper();
}
