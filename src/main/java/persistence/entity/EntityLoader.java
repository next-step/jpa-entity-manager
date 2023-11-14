package persistence.entity;

import java.util.List;
import java.util.Optional;

public interface EntityLoader<T> {
  <T> Optional<T> load(Long id);
  List<T> findAll();

  List<T> loadByIds(List<Long> ids);
}
