package persistence.entity;

import jakarta.persistence.Entity;
import persistence.exception.PersistenceException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityScanner {
    private static final char DOT = '.';
    private static final char FILE_SEPARATOR = '/';
    private static final String FILE = "file";
    private static final String JAR = "jar";
    private static final String CLASS_FILE_EXTENSION = ".class";
    private final List<Class<?>> entityClasses;

    public EntityScanner(final Class<?> baseClass) {
        this.entityClasses = scan(baseClass);
    }

    public List<Class<?>> getEntityClasses() {
        return this.entityClasses;
    }

    private List<Class<?>> scan(final Class<?> baseClass) {
        if (baseClass.isAnnotationPresent(EntityScanBase.class)) {
            final EntityScanBase annotation = baseClass.getAnnotation(EntityScanBase.class);
            final String[] packageNames = annotation.packageNames();
            return scanPackages(packageNames);
        }

        return scanPackages(baseClass.getPackage().getName());
    }

    private List<Class<?>> scanPackages(final String... packagePaths) {
        return Arrays.stream(packagePaths)
                .map(packageName -> scanPackage(getPackageFullPath(packageName), packageName))
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    private Path getPackageFullPath(final String packageName) {
        try {
            final String packageRelativePath = getFilePath(packageName);
            final URI packageUri = getPackageUri(packageRelativePath);
            if (isSchemeFile(packageUri)) {
                return Paths.get(packageUri);
            } else if (isSchemeJar(packageUri)) {
                return getPathInJar(packageUri, packageRelativePath);
            }
        } catch (final URISyntaxException | IOException e) {
            throw new PersistenceException("packageFullPath 를 가져오는것에 실패했습니다", e);
        }
        throw new PersistenceException("packageFullPath 를 가져오는것에 실패했습니다");
    }

    private static Path getPathInJar(final URI packageUri, final String packageRelativePath) throws IOException {
        final FileSystem fileSystem = FileSystems.newFileSystem(packageUri, Collections.emptyMap());
        final Path packageFullPathInJar = fileSystem.getPath(packageRelativePath);
        fileSystem.close();
        return packageFullPathInJar;
    }

    private String getFilePath(final String packageName) {
        return packageName.replace(DOT, FILE_SEPARATOR);
    }

    private URI getPackageUri(final String packageRelativePath) throws URISyntaxException {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(packageRelativePath);
        if (Objects.isNull(resource)) {
            throw new PersistenceException("Entity Scan 중 resource 가져오기에 실패했습니다.");
        }
        return resource.toURI();
    }

    private boolean isSchemeJar(final URI packageUri) {
        return JAR.equals(packageUri.getScheme());
    }

    private boolean isSchemeFile(final URI packageUri) {
        return FILE.equals(packageUri.getScheme());
    }

    private List<Class<?>> scanPackage(final Path packagePath, final String packageName) {
        if (!Files.exists(packagePath)) {
            return Collections.emptyList();
        }

        final Map<Boolean, List<Path>> partitionedFiles = partitionRegularFiles(packagePath);
        final List<Path> regularFiles = partitionedFiles.get(true);
        final List<Path> nonFiles = partitionedFiles.get(false);

        final List<Class<?>> entityClasses = getEntityClasses(packageName, regularFiles);
        final List<Class<?>> nestedClasses = getNestedEntityClasses(packageName, nonFiles);
        entityClasses.addAll(nestedClasses);
        return entityClasses;
    }

    private Map<Boolean, List<Path>> partitionRegularFiles(final Path packagePath) {
        try (final Stream<Path> list = Files.list(packagePath)) {
            return list.collect(Collectors.partitioningBy(Files::isRegularFile));
        } catch (final IOException e) {
            throw new PersistenceException("파일을 분리하는 도중 에러가 발생했습니다.", e);
        }
    }

    private List<Class<?>> getNestedEntityClasses(final String packageName, final List<Path> nonFiles) {
        return nonFiles.stream()
                .filter(Files::isDirectory)
                .map(path -> scanPackage(path, packageName + DOT + path.getFileName()))
                .flatMap(List::stream)
                .filter(clazz -> clazz.isAnnotationPresent(Entity.class))
                .collect(Collectors.toList());
    }

    private List<Class<?>> getEntityClasses(final String packageName, final List<Path> regularFiles) {
        return regularFiles.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(this::isClassFile)
                .map(fileName -> createClassBy(packageName, fileName))
                .filter(clazz -> clazz.isAnnotationPresent(Entity.class))
                .collect(Collectors.toList());
    }

    private boolean isClassFile(final String fileName) {
        return fileName.endsWith(CLASS_FILE_EXTENSION);
    }

    private Class<?> createClassBy(final String packageName, final String fileName) {
        final String classFullName = packageName + DOT + fileName.replaceFirst("\\.class$", "");
        try {
            return Class.forName(classFullName);
        } catch (final ClassNotFoundException e) {
            throw new PersistenceException(classFullName + " 클래스를 찾을 수 없습니다.", e);
        }
    }


}
