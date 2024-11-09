package persistence.fixture;

public class SimpleEntityFixture {
    private int field1;
    private long field2;
    private String field3;

    public SimpleEntityFixture() {

    }

    public SimpleEntityFixture(int field1, long field2, String field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public void setField2(long field2) {
        this.field2 = field2;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

}
