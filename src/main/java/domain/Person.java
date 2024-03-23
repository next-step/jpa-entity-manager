package domain;

import jakarta.persistence.*;

import java.util.Objects;

@Table(name = "users")
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nick_name")
    private String name;

    @Column(name = "old")
    private Integer age;

    @Column(nullable = false)
    private String email;

    @Transient
    private Integer index;

    public Person() {

    }

    public Person(Long id, String name, Integer age, String email, Integer index) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.index = index;
    }

    public Person(String name, Integer age, String email, Integer index) {
        this(null, name, age, email, index);
    }

    public Person(Long id, String name, Integer age, String email) {
        this(id, name, age, email, null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String newName) {
        this.name = newName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Person person = (Person) object;
        return Objects.equals(id, person.id) && Objects.equals(name, person.name) && Objects.equals(age, person.age) && Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, email);
    }
}
