package persistence.core;

import java.util.List;

public interface EntityLoader {

    <T> List<T> find(Class<T> clazz) throws Exception;
    <T> T find(Class<T> clazz, Long id) throws Exception;

}
