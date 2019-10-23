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
package com.learn.platformutil;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Locale;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 10:10
 * 数字的工具类：
 *  1. 原始类型数字与byte[]的双向转换（via Guava）
 *  2. 判断字符串是否是数字，是否16进制字符串（via Common Lang）
 *  3. 10进制/16进制字符串 与 原始类型数字/数字对象的双向转换
 *  *  */
public class NumberUtil {
    /**
     * bytes[] 与原始类型数字转换
     * */
    public static byte[] toBytes(int value) {
        return Ints.toByteArray(value);
    }

    public static byte[] toBytes(long value) {
        return Longs.toByteArray(value);
    }

    public static byte[] toBytes(double value) {
        return toBytes(Double.doubleToRawLongBits(value));
    }

    public static int toInt(byte[] bytes) {
        return Ints.fromByteArray(bytes);
    }

    public static long toLong(byte[] bytes) {
        return Longs.fromByteArray(bytes);
    }

    public static double toDouble(byte[] bytes) {
        return Double.longBitsToDouble(toLong(bytes));
    }

    /**
     * 判断字符串是否是合法数字
     * */
    public static boolean isNumber(String str) {
        return NumberUtils.isCreatable(str);
    }

    /**
     * 判断字符串是否16进制
     * */
    public static boolean isHexNumber(String value) {
        int index = value.startsWith("-") ? 1 : 0;
        return value.startsWith("0x", index) || value.startsWith("0X", index) ||
            value.startsWith("#", index);
    }

    /**
     * 将字符串安全的转换成原始类型数字
     * */
    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        return NumberUtils.toInt(str, defaultValue);
    }

    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    public static long toLong(String str, long defaultValue) {
        return NumberUtils.toLong(str, defaultValue);
    }

    public static double toDouble(String str) {
        return toDouble(str, 0L);
    }

    public static double toDouble(String str, double defaultValue) {
        return NumberUtils.toDouble(str, defaultValue);
    }

    public static Integer toIntObject(String str) {
        return toIntObject(str, null);
    }

    public static Integer toIntObject(String str, Integer defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(str);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Long toLongObject(String str) {
        return toLongObject(str, null);
    }

    public static Long toLongObject(String str, Long defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Long.valueOf(str);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Double toDoubleObject(String str) {
        return toDoubleObject(str, null);
    }

    public static Double toDoubleObject(String str, Double defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }
        try {
            return Double.valueOf(str);
        } catch (final NumberFormatException e){
            return defaultValue;
        }
    }

    /**
     * 16进制 字符串转换为数字对象
     * */
    public static Integer hexToInxObject(String str) {
        return hexToInxObject(str, null);
    }

    public static Integer hexToInxObject(String str, Integer defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return Integer.decode(str);
        }catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Long hexToLongObject(String str) {
        return hexToLongObject(str, null);
    }

    public static Long hexToLongObject(String str, Long defaultValue) {
        if (StringUtils.isEmpty(str)) {
            return defaultValue;
        }

        try {
            return Long.decode(str);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * toString（定义了原子类型与对象类型的参数，保证不会用错函数）
     * */
    public static String toString(int i) {
        return Integer.toString(i);
    }

    public static String toString(Integer i) {
        return Integer.toString(i);
    }

    public static String toString(long l) {
        return Long.toString(l);
    }

    public static String toString(Long l) {
        return Long.toString(l);
    }

    public static String toString(double d) {
        return Double.toString(d);
    }

    public static String toString(Double d) {
        return Double.toString(d);
    }

    /**
     * 安全的将小于Integer.MAX的龙转为int，否则抛出IllegalArgumentException异常
     * */
    public static int toInt32(long x) {
        int x1 = (int) x;
        if (x1 == x) {
            return x1;
        }

        throw new IllegalArgumentException("Inx " + x + " out of range");
    }

    /**
     * 输出格式化位小数后两位的double字符串
     * @since 1.6+
     * */
    public static String to2DigitString(double d) {
        return String.format(Locale.ROOT, "%.2f", d);
    }
}