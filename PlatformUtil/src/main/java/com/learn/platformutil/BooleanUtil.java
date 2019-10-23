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

import org.apache.commons.lang3.BooleanUtils;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 9:56
 * 布尔工具
 */
public class BooleanUtil {
    /**
     * 使用标准JDK，只分析是否忽略大小写的"true"，为空则返回false
     * */
    public static boolean toBoolean(String string) {
        return Boolean.parseBoolean(string);
    }

    /**
     * 使用标准的JDK，只分析是否忽略大小写的"true"，为空则返回null
     * */
    public static boolean toBooleanObject(String string) {
        return string != null ? Boolean.valueOf(string) : null;
    }

    /**
     * 使用标准JDK，只分析是否忽略大小写的"true"，为空返回defaultValue
     * */
    public static boolean toBooleanObject(String str, Boolean defaultValue) {
        return str != null ? Boolean.valueOf(str) : defaultValue;
    }

    /**
     * 支持true/false,no/off,y/n,yes/no的转换，str为空或无法分析时返回null
     * */
    public static Boolean parseGeneralString(String str) {
        return BooleanUtils.toBooleanObject(str);
    }

    /**
     * 支持true/false,no/off,y/n,yes/no的转换，str为空或者无法分析时返回defaultValue
     * */
    public static Boolean parseGeneralString(String str, Boolean defaultValue) {
        return BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBooleanObject(str), defaultValue);
    }

    /**
     * 取反
     * */
    public static boolean negate(final boolean bool) {
        return !bool;
    }

    /**
     * 取反
     * */
    public static Boolean negate(final Boolean bool) {
        return BooleanUtils.negate(bool);
    }

    /**
     * 多个值的and
     * */
    public static boolean and(final boolean... array) {
        return BooleanUtils.and(array);
    }

    /**
     * 多个值的or
     * */
    public static boolean or(final boolean... array) {
        return BooleanUtils.or(array);
    }

}