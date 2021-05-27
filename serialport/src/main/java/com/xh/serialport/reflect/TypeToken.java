package com.xh.serialport.reflect;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeToken<T> {



    private final Class<? super T> rawType;//泛型类型
    private final Type type;//当前类型

    public TypeToken() {
        this.type = getSuperclassTypeParameter(getClass());
        rawType = (Class<? super T>) getRawType(type);
    }

    public TypeToken(Type type) {
        this.type = type;
        this.rawType = (Class<? super T>) getRawType(type);
    }

    public Class<? super T> getRawType() {
        return rawType;
    }

    public Type getType() {
        return type;
    }

    public static TypeToken<?> get(Type type) {
        return new TypeToken<Object>(type);
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof TypeToken && rawType == ((TypeToken) obj).rawType;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    public static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            // type is a normal class.
            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class.
            // Neal isn't either but suspects some pathological case related
            // to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;

        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();

        } else if (type instanceof TypeVariable) {
            // we could use the variable's bounds, but that won't work if there are multiple.
            // having a raw type that's more general than necessary is okay
            return Object.class;

        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);

        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                    + "GenericArrayType, but <" + type + "> is of type " + className);
        }
    }
}
