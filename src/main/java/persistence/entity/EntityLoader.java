package persistence.entity;

public interface EntityLoader {
  <T> T load(Long id);
}
