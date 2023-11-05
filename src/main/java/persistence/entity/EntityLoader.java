package persistence.entity;

import java.util.List;

public interface EntityLoader<T> {
  <T> T load(Long id);
  List<T> findAll();
}
