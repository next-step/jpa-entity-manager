package persistence.support;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.extension.*;

public class DatabaseSetupExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private DatabaseServer server;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        server = new H2();
        server.start();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

        getStore(context).put("jdbcTemplate", jdbcTemplate);
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(DatabaseSetupExtension.class, context.getRequiredTestClass()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        server.stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == JdbcTemplate.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getStore(extensionContext).get("jdbcTemplate");
    }
}
