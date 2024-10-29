package persistence.model;

import persistence.model.exception.ColumnNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityTableColumns {
    private List<EntityColumn> columns = new ArrayList<>();

    public EntityTableColumns(List<EntityColumn> columns) {
        this.columns = columns;
    }

    public EntityTableColumns() {
    }

    public void setColumns(List<EntityColumn> columns) {
        this.columns = columns;
    }

    public List<EntityColumn> getAll() {
        return columns;
    }

    public EntityColumn getPrimaryColumn() {
        // XXX: 복합키 고려 X
        return columns.stream()
                .filter(EntityColumn::isPrimary)
                .findFirst()
                .orElseThrow(() -> new ColumnNotFoundException("PRIMARY KEY NOT FOUND!"));
    }

    public List<EntityColumn> getNonPrimaryColumns() {
        return columns.stream()
                .filter(column -> !column.isPrimary())
                .collect(Collectors.toList());
    }

    public EntityColumn findByName(String name) {
        return columns.stream()
                .filter(column -> column.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ColumnNotFoundException(name));
    }
}
