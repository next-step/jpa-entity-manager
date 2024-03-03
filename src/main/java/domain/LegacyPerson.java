package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LegacyPerson {

    @Id
    private Long id;

    private String name;

    private Integer age;

    public LegacyPerson(Long id,
                        String name,
                        Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
