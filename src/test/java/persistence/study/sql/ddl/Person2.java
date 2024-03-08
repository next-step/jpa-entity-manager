package persistence.study.sql.ddl;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Person2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nick_name")
    private String name;

    @Column(name = "old")
    private Integer age;

    @Column(nullable = false)
    private String email;

    protected Person2() {

    }

    public Person2(String name, Integer age, String email) {
        this(null, name, age, email);
    }

    public Person2(Long id, String name, Integer age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person2 person2 = (Person2) o;

        if (!Objects.equals(id, person2.id)) return false;
        if (!Objects.equals(name, person2.name)) return false;
        if (!Objects.equals(age, person2.age)) return false;
        return Objects.equals(email, person2.email);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
