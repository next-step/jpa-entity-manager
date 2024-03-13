package persistence;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.model.EntityMetaDataMapping;
import persistence.sql.ddl.PersonV1;
import persistence.sql.ddl.PersonV2;
import persistence.sql.ddl.PersonV3;

public abstract class EntityMetaDataTestSupport {
    private static final Logger log = LoggerFactory.getLogger(EntityMetaDataTestSupport.class);
    @BeforeAll
    static void setUpEntityMetaDataTestSupportClass() {
        log.info("set up test class");
        EntityMetaDataMapping.putMetaData(PersonV1.class);
        EntityMetaDataMapping.putMetaData(PersonV2.class);
        EntityMetaDataMapping.putMetaData(PersonV3.class);
    }
}
