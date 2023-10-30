package persistence.testFixtures;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Objects;

@Table(name = "person")
@Entity
public class DifferentPerson {

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

    public DifferentPerson() {
    }

    public DifferentPerson(String name, Integer age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public DifferentPerson(Long id, String name, Integer age, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
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

    public void changeEmail(String email) {
        this.email = email;
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DifferentPerson)) {
            return false;
        }
        DifferentPerson person = (DifferentPerson) object;
        return Objects.equals(id, person.id) && Objects.equals(name, person.name)
                && Objects.equals(age, person.age) && Objects.equals(email, person.email)
                && Objects.equals(index, person.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, email, index);
    }
}
