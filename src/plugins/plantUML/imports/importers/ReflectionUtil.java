package plugins.plantUML.imports.importers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {


    public static <T> T getPrivateField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field: " + fieldName, e);
        }
    }
    
    /**
     * Makes all fields of a class accessible.
     *
     * @param clazz The class to make fields accessible.
     */
    public static void makeFieldsAccessible(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // Make the field accessible
        }
    }

    /**
     * Makes all methods of a class accessible.
     *
     * @param clazz The class to make methods accessible.
     */
    public static void makeMethodsAccessible(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true); // Make the method accessible
        }
    }

    /**
     * Gets the value of a private field.
     *
     * @param obj       The object containing the field.
     * @param fieldName The name of the field.
     * @return The value of the field.
     * @throws Exception if the field is not found or cannot be accessed.
     */
    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * Sets the value of a private field.
     *
     * @param obj       The object containing the field.
     * @param fieldName The name of the field.
     * @param value     The value to set.
     * @throws Exception if the field is not found or cannot be accessed.
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /**
     * Invokes a private method.
     *
     * @param obj        The object containing the method.
     * @param methodName The name of the method.
     * @param args       The arguments to pass to the method.
     * @return The result of the method invocation.
     * @throws Exception if the method is not found or cannot be accessed.
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) throws Exception {
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        Method method = obj.getClass().getDeclaredMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }
}
