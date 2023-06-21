package persistence.dialect;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DdlTypeTest {

    @Test
    void getTypeName() {
        String pattern = "varchar($l)";
        DdlType ddlType = new DdlType(pattern);

        Long size = 255L;
        String actual = ddlType.getTypeName(size);

        assertThat(actual).isEqualTo("varchar(255)");
    }

}
