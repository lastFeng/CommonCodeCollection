/*
 * Copyright 2001-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.learn.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/24 10:39
 *
 * 反射工具类：
 *  提供调用getter/setter方法，访问私有变量，调用私有方法，获取泛型类型Class，被AOP过的真实类等工具
 */
@SuppressWarnings("rawtypes")
public class Reflections {

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";
    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * 调用Getter方法
     * 支持多级：如：对象名.对象名.方法
     * */
    public static <E> Object invokeGetter(E obj, String propertyName) {
        Object object = obj;
        for (String name: StringUtils.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
        }
        return object;
    }

    /**
     * 调用setter方法，仅匹配方法名
     * */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");

        for (int i = 0; i < names.length; i++) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[]{}, new Object[]{});
            } else {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[]{value});
            }
        }
    }

    /**
     * 直接读取对象属性值，无视private/protected修饰符，不经过getter函数
     * */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target ["+ obj + "]");
        }

        Object result = null;

        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 直接设置对象属性，无视private/protected修饰符，不经过setter函数
     * */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target ["+ obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接调用对象方法，无视private/protected修饰符
     * */
    public static <E> Object invokeMethod(final E obj, final String methodName, final Class<?>[] parameterTypes,
                                          final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);

        if (method == null) {
            throw new IllegalArgumentException("Could not find field [" + methodName + "] on target ["+ obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }


    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);

        if (method == null) {
            throw new IllegalArgumentException("Could not find field [" + methodName + "] on target ["+ obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(fieldName, "fieldName can't be blank");

        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                makeAccessible(field);
                return field;
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
        return null;
    }

    public static Method getAccessibleMethod(final Object obj, final String methodName,
                                             final Class<?>... parameterTypes) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            try {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                makeAccessible(method);
                return method;
            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        return null;
    }

    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
        Validate.notNull(obj, "object can't be null");
        Validate.notBlank(methodName, "methodName can't be blank");

        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method: methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
        && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
            Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 通过反射，获得Class定义中声明的泛型参数的类型，注意泛型必须定义在父类处
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenricType(final Class clazz) {
        return getClassGenricType(clazz, 0);
    }

    public static Class getClassGenricType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }

        return (Class) params[index];
    }

    public static Class<?> getUserClass(Object instance) {
        Validate.notNull(instance, "Instance must not be null");

        Class clazz = instance.getClass();
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }

    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
        || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException)e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception " + e);
    }
}