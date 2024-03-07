package persistence.sql.dml;

import persistence.sql.mapping.Table;

import java.util.Collections;
import java.util.List;

public class Select {

    private final Table table;

    private final Wheres wheres;

    public Select(final Table table) {
        this(table, Collections.emptyList());
    }

    public Select(final Table table, final List<Where> wheres) {
        this.table = table;
        this.wheres = new Wheres(wheres);
    }

    public Table getTable() {
        return table;
    }

    public List<Where> getWheres() {
        return this.wheres.getWheres();
    }
}
