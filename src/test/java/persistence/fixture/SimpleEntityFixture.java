package persistence.fixture;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SimpleEntityFixture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer number;

    public SimpleEntityFixture() {

    }

    public SimpleEntityFixture(long id, String name, int number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public SimpleEntityFixture(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
