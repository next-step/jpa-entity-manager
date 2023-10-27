package persistence.entity;

public class EntityManagerFactory {
    private EntityManagerFactory() {}

    private static class EntityManagerFactoryHolder {
        private static final EntityManagerFactory INSTANCE = new EntityManagerFactory();
    }

    public static EntityManagerFactory getInstance() {
        return EntityManagerFactoryHolder.INSTANCE;
    }

//    public EntityManager createEntityManager() {
//        //return new DefaultEntityManager();
//    }

}
