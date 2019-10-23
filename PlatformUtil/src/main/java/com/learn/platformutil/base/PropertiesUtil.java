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
package com.learn.platformutil.base;

import com.learn.platformutil.io.URLResourceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 9:48
 * 关于Properties的工具类：
 *  1. 统一读取Properties
 *  2. 从文件或字符串装载Properties
 */
public class PropertiesUtil {
    /**
     * 读取Properties
     * */
    public static Boolean getBoolean(Properties props, String name, Boolean defaultValue) {
        return BooleanUtil.toBooleanObject(props.getProperty(name), defaultValue);
    }

    public static Integer getInt(Properties props, String name, Integer defaultValue) {
        return NumberUtil.toIntObject(props.getProperty(name), defaultValue);
    }

    public static Long getLong(Properties props, String name, Long defaultValue) {
        return NumberUtil.toLongObject(props.getProperty(name), defaultValue);
    }

    public static Double getDouble(Properties props, String name, Double defaultValue) {
        return NumberUtil.toDoubleObject(props.getProperty(name), defaultValue);
    }

    public static String getString(Properties props, String name, String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    /**
     * 从文件路径加载properties
     *
     * 路径支持从外部或resources文件加载，“file://”或无前缀代表外部文件，“classpath://”代表resources
     * */
    public static Properties loadFromFile(String generalPath) {
        Properties props = new Properties();

        try(InputStream in = URLResourceUtil.asStream(generalPath)) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public static Properties loadFromString(String content) {
        Properties props = new Properties();

        try (Reader reader = new StringReader(content)){
            props.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}