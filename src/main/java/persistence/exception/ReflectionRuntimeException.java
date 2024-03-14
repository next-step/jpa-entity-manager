package persistence.exception;

public class ReflectionRuntimeException extends RuntimeException {
    public ReflectionRuntimeException(Object object, Exception e) {
        this(object.getClass(), e);
    }

    public ReflectionRuntimeException(Class<?> clazz, Exception e) {
        super("Reflection error on class: " + clazz.getName(), e);
    }
}
