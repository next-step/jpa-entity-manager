package persistence.testFixtures;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PkHasPerson {

    @Id
    private Long id;

    private String name;

    private Integer age;
    protected PkHasPerson() {

    }

    public PkHasPerson(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
