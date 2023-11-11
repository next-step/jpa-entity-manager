package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class CloneUtils {

  private CloneUtils() {

  }

  public static Object clone(Object entity) {
    try {
      Class<?> clazz = entity.getClass();
      Constructor<?> constructor = getConstructor(clazz);

      Object cloned = constructor.newInstance();

      Field[] fields = clazz.getDeclaredFields();

      for (Field field : fields) {
        field.setAccessible(true);
        Object value = field.get(entity);
        field.set(cloned, value);
      }
      return cloned;

    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException e) {
      throw new RuntimeException("생성자 오류", e);
    }
  }

  private static Constructor<?> getConstructor(Class<?> clazz) throws NoSuchMethodException {

    Constructor<?> constructor = clazz.getDeclaredConstructor();
    constructor.setAccessible(true);
    return constructor;
  }
}
