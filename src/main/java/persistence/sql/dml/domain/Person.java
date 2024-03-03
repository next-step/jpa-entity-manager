package persistence.sql.dml.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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

    public Person(Long id) {
        this.id = id;
    }

    public Person(String name, Integer age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public Person(final long id, final String name, final int age, final String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public Integer getIndex() {
        return index;
    }


    public void changeAge(final int age) {
        this.age = age;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final Person person = (Person) object;
        return Objects.equals(id, person.id) && Objects.equals(name, person.name) && Objects.equals(age, person.age) && Objects.equals(email, person.email) && Objects.equals(index, person.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, email, index);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", index=" + index +
                '}';
    }
}