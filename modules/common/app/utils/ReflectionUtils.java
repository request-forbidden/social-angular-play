package utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static Field getFieldUpTo(String fieldName, Class<?> startClass, Class<?> exclusiveParent) throws NoSuchFieldException {
        Field currentClassField = null;
        try {
            currentClassField = startClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        }
        Class<?> parentClass = startClass.getSuperclass();

        if (currentClassField == null && parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            Field parentClassField = getFieldUpTo(fieldName, parentClass, exclusiveParent);
            if(parentClassField != null) return parentClassField;
        }

        if(currentClassField == null) throw new NoSuchFieldException(fieldName);

        return currentClassField;
    }

}
