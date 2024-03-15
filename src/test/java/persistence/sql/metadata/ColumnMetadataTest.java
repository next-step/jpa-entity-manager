package persistence.sql.metadata;

import domain.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


class ColumnMetadataTest {

    @Test
    @DisplayName("ColumnMetadata Annotation 생성 테스트 - Transient Field 제외")
    void generateColumnAnnotations() {
        // given
        List<ColumnMetadata> columns = EntityMetadata.from(Person.class).getColumns();

        // when
        List<Annotation> annotations = columns.stream()
                .flatMap(column -> column.getAnnotations().stream())
                .filter(annotation -> !annotation.annotationType().equals(Transient.class))
                .collect(Collectors.toList());

        // then
        assertThat(annotations).filteredOn(annotation -> annotation.annotationType().equals(Transient.class)).isEmpty();
    }

    @Test
    @DisplayName("ColumnMetadata Column Name 생성 테스트 - Column Annotation 존재")
    void generateColumnName() {
        List<ColumnMetadata> columns = EntityMetadata.from(Person.class).getColumns();

        // when
        List<ColumnMetadata> existsColumnAnnotationColumns = columns.stream()
                .filter(columnMetadata -> columnMetadata.getAnnotations().stream()
                        .anyMatch(annotation -> annotation.annotationType().equals(Column.class)))
                .collect(Collectors.toList());

        // then
        assertThat(existsColumnAnnotationColumns).extracting(ColumnMetadata::getName)
                .contains("nick_name", "old", "email");
    }

}