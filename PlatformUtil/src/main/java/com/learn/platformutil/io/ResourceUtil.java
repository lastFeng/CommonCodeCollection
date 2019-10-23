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
package com.learn.platformutil.io;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang3.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 11:04
 * 针对Jar博阿内的文件的工具类：
 * 1. ClassLoader
 * 不指定contextClass时，优先使用Thread.getContextClassLoader()，如果未设置则使用Guava Resources的ClassLoader
 * 2. 路径
 * 不指定contextClass时，按URLClassLoader的实现，从jar file中查找resourceName
 * 指定contextClass时，class.getResource()回西安对那么进行处理再交给ClassLoader
 * 3. 同名资源
 * 如果有多个同名资源，处分调用getResources()获取全部资源，否则在URLClassLoader中按ClassPath顺序打开第一个命中的Jar文件
 */
public class ResourceUtil {
    /**
     * 打开单个文件
     * */
    public static URL asUrl(String resourceName) {
        return Resources.getResource(resourceName);
    }

    public static URL asUrl(Class<?> contextClass, String resourceName) throws IOException {
        return Resources.getResource(contextClass, resourceName);
    }

    public static InputStream asStream(String resourceName) throws IOException{
        return Resources.getResource(resourceName).openStream();
    }

    public static InputStream asStream(Class<?> contextClass, String resourceName) throws IOException {
        return Resources.getResource(contextClass, resourceName).openStream();
    }

    /**
     * 读取单个文件
     * */
    public static String toString(String resourceName) throws IOException {
        return Resources.toString(asUrl(resourceName), Charsets.UTF_8);
    }

    public static String toString(Class<?> contextClass, String resourceName) throws IOException {
        return Resources.toString(asUrl(contextClass, resourceName), Charsets.UTF_8);
    }

    public static List<String> toLines(String resourceName) throws IOException {
        return Resources.readLines(asUrl(resourceName), Charsets.UTF_8);
    }

    public static List<String> toLines(Class<?> contextClass, String resourceName) throws IOException {
        return Resources.readLines(asUrl(contextClass, resourceName), Charsets.UTF_8);
    }

    /**
     * 打开所有同名文件
     * */
    public static List<URL> getReousrces(String resourceName) {
        return getReousrces(resourceName, getDefaultClassLoader());
    }

    public static List<URL> getReousrces(String resourceName, ClassLoader contextClassLoader) {
        try {
            Enumeration<URL> urls = contextClassLoader.getResources(resourceName);
            List<URL> list = new ArrayList<URL>(10);
            while (urls.hasMoreElements()) {
                list.add(urls.nextElement());
            }
            return list;
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 获取ClassLoader
     * */
    private static ClassLoader getDefaultClassLoader() {
        ClassLoader c1 = null;
        try {
            c1 = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {

        }

        if (c1 == null) {
            c1 = ClassUtils.class.getClassLoader();
            if (c1 == null) {
                try {
                    c1 = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {

                }
            }
        }
        return c1;
    }
}