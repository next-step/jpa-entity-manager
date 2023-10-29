package persistence.entity;

import jakarta.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdbc.JdbcTemplate;
import persistence.exception.EntityManagerInitException;
import persistence.sql.dml.Query;

public class EntityManagerFactory {

    private static final String ROOT_PACKAGE_NAME = "domain";

    public static EntityManager of(Connection connection) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        List<Class<?>> classList = EntityManagerFactory.getClasses();

        return new EntityManagerImpl(persisterInit(jdbcTemplate, classList));
    }

    private static Map<String, EntityPersister<?>> persisterInit(JdbcTemplate jdbcTemplate, List<Class<?>> list) {
        Map<String, EntityPersister<?>> map = new HashMap<>();

        list.forEach(aClass -> map.put(aClass.getName(), new EntityPersister<>(jdbcTemplate, aClass, Query.getInstance())));

        return map;
    }

    private static List<Class<?>> getClasses() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(ROOT_PACKAGE_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Collections.list(resources).stream()
            .map(URL::getFile)
            .map(File::new)
            .flatMap(directory -> {
                try {
                    return findClasses(directory, ROOT_PACKAGE_NAME).stream();
                } catch (ClassNotFoundException e) {
                    throw new EntityManagerInitException(e);
                }
            })
            .collect(Collectors.toList());
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".class") || new File(dir, name).isDirectory());
        if (files == null) {
            return new ArrayList<>();
        }

        List<Class<?>> classes = new ArrayList<>();

        Arrays.stream(files).forEach(file -> {
            try {
                handleDirectoryContents(packageName, classes, file);
            } catch (ClassNotFoundException e) {
                throw new EntityManagerInitException(e);
            }
        });

        return classes;
    }

    private static void handleDirectoryContents(String packageName, List<Class<?>> classes, File file) throws ClassNotFoundException {
        if (file.isDirectory()) {
            classes.addAll(findClasses(file, packageName + "." + file.getName()));
        } else {
            extractedEntityClass(packageName, classes, file);
        }
    }

    private static void extractedEntityClass(String packageName, List<Class<?>> classes, File file)
        throws ClassNotFoundException {
        Class<?> clazz = Class.forName(String.join(".", packageName, file.getName().replace(".class", "")));
        if (clazz.isAnnotationPresent(Entity.class)) {
            classes.add(clazz);
        }
    }
}
