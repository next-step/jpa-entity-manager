package persistence.testFixtures;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Objects;

@Entity
@Table(name = "users")
public class NoAutoIncrementPerson {

    @Id
    private Long id;

    @Column(name = "nick_name")
    private String name;

    @Column(name = "old")
    private Integer age;

    @Column(nullable = false)
    private String email;

    @Transient
    private Integer index;


    public NoAutoIncrementPerson() {
    }

    public NoAutoIncrementPerson(Long id ,String name, Integer age, String email) {
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

    public Integer getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof NoAutoIncrementPerson)) {
            return false;
        }
        NoAutoIncrementPerson that = (NoAutoIncrementPerson) object;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(age, that.age) && Objects.equals(email, that.email)
                && Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, email, index);
    }
}
