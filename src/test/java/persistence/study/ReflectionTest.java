package persistence.study;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionTest {
    private static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);

    @Test
    @DisplayName("Car 객체 정보 가져오기")
    void showClass() {
        Class<Car> carClass = Car.class;
        logger.debug("Class Name: " + carClass.getName());

        logger.debug("--- Fields ---");
        Field[] fields = carClass.getDeclaredFields();
        for (Field field : fields) {
            logger.debug(
                "Field: " + field.getName() +
                ", Type: " + field.getType().getName() +
                ", Modifiers: " + field.getModifiers()
            );
        }

        logger.debug("--- Constructors ---");
        Constructor<?>[] constructors = carClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            logger.debug(
                "Constructor: " + constructor.getName() +
                ", Parameter Count: " + constructor.getParameterCount() +
                ", Modifiers: " + constructor.getModifiers()
            );
        }

        logger.debug("--- Methods ---");
        Method[] methods = carClass.getDeclaredMethods();
        for (Method method : methods) {
            logger.debug(
                "Method: " + method.getName() +
                ", Parameter Count: " + method.getParameterCount() +
                ", Return Type: " + method.getReturnType().getName() +
                ", Modifiers: " + method.getModifiers()
            );
        }
    }

    @Test
    @DisplayName("test로 시작하는 메소드 실행")
    void testMethodRun() throws Exception {
        Car tesla = new Car("Tesla", 10000);
        Class<? extends Car> carClass = tesla.getClass();

        for (Method declaredMethod : carClass.getDeclaredMethods()) {
            if (declaredMethod.getName().startsWith("test")) {
                Object invoke = declaredMethod.invoke(tesla);
                System.out.println(invoke);
            }
        }
    }

    @Test
    @DisplayName("@PrintView 애노테이션 메소드 실행")
    void testAnnotationMethodRun() throws Exception {
        Class<Car> carClass = Car.class;
        Method[] methods = carClass.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(PrintView.class)) {
                method.invoke(carClass.newInstance());
            }
        }
    }

    @Test
    @DisplayName("private field에 값 할당")
    void privateFieldAccess() throws Exception {
        Car car = new Car();
        Class<Car> carClass = Car.class;

        Field nameField = carClass.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(car, "소나타");

        Field priceField = carClass.getDeclaredField("price");
        priceField.setAccessible(true);
        priceField.set(car, 3000);

        assertThat(car.testGetName()).isEqualTo("test : 소나타");
        assertThat(car.testGetPrice()).isEqualTo("test : " + 3000);
    }

    @Test
    @DisplayName("인자를 가진 생성자의 인스턴스 생성")
    void constructorWithArgs() throws Exception {
        Class<Car> carClass = Car.class;
        Constructor<Car> constructor = carClass.getDeclaredConstructor(String.class, int.class);

        Car carInstance = constructor.newInstance("소나타", 3000);
        assertThat(carInstance.testGetName()).isEqualTo("test : 소나타");
        assertThat(carInstance.testGetPrice()).isEqualTo("test : " + 3000);
    }

}
