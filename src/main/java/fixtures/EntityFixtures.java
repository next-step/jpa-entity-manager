package fixtures;

import jakarta.persistence.*;

public class EntityFixtures {
    @Entity
    @Table(name = "entity_name")
    public static class SampleOneWithValidAnnotation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;
        @Column(name = "name", length = 200)
        String name;
        @Column(name = "old")
        Integer age;

        public SampleOneWithValidAnnotation() {
        }

        public SampleOneWithValidAnnotation(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public SampleOneWithValidAnnotation(long id, String nickName, int age) {
            this.id = id;
            this.name = nickName;
            this.age = age;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return "SampleOneWithValidAnnotation{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    @Entity
    @Table(name = "two")
    public static class SampleTwoWithValidAnnotation {
        @Id
        @Column(name = "two_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;
        @Column(name = "nick_name", length = 200, nullable = false)
        String name;
        @Column
        Long age;

        public SampleTwoWithValidAnnotation() {

        }

        public SampleTwoWithValidAnnotation(long id, String nickName, Long age) {
            this.id = id;
            this.name = nickName;
            this.age = age;
        }

        @Override
        public String toString() {
            return "SampleTwoWithValidAnnotation{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    @Entity
    public static class EntityWithMultiIdAnnotation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;
        @Id
        @Column(name = "name", length = 200)
        String name;
        @Column(name = "age")
        Integer age;
    }

    public static class EntityWithOutEntityAnnotation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;
        @Id
        @Column(name = "name", length = 200)
        String name;
        @Column(name = "age")
        Integer age;
    }

    @Table(name = "entity_with_string_id")
    @Entity
    public static class EntityWithStringId {
        @Id
        String id;
        @Column(name = "name", length = 200)
        String name;
        @Column(name = "age")
        Integer age;

        public EntityWithStringId() {
        }

        public EntityWithStringId(String id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    @Table(name = "entity_with_Integer_id")
    @Entity
    public static class EntityWithIntegerId {
        @Id
        @Column(name = "entity_with_integer_id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        Integer id;
        @Column(name = "name", length = 200)
        String name;
        @Column(name = "age")
        Integer age;

        public EntityWithIntegerId(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public EntityWithIntegerId(Integer id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public EntityWithIntegerId() {

        }

        public Integer getId() {
            return id;
        }

        @Override
        public String toString() {
            return "EntityWithIntegerId{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
