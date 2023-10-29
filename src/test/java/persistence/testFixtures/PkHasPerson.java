package persistence.testFixtures;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PkHasPerson {

    @Id
    private Long id;

    private String name;

    private Integer age;

    public PkHasPerson(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
