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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 10:59
 * 兼容url位无前缀，file://与classpath:// 的工具集
 */
public class URLResourceUtil {
    private static final String CLASSPATH_PREFIX = "classpath://";
    private static final String URL_PROTOCOL_FILE = "file";

    /**
     * 兼容url位无前缀，file://与classpath:// 的文件获取
     * 如果以classpath:// 定义的文件不存在会抛出IllegalArgumentException异常，以file://定义则不会
     * */
    public static File asFile(String generalPath) throws IOException {
        if (StringUtils.startsWith(generalPath, CLASSPATH_PREFIX)) {
            String resourceName = StringUtils.substringAfter(generalPath, CLASSPATH_PREFIX);
            return getFileByURL(ResourceUtil.asUrl(resourceName));
        }

        try {
            // try URL
            return getFileByURL(new URL(generalPath));
        } catch (MalformedURLException e) {
            // no URL --> treat as a file path
            return new File(generalPath);
        }
    }

    public static InputStream asStream(String generalPath) throws IOException {
        if (StringUtils.startsWith(generalPath, CLASSPATH_PREFIX)) {
            String resourceName = StringUtils.substringAfter(generalPath, CLASSPATH_PREFIX);
            return ResourceUtil.asStream(resourceName);
        }

        try {
            // try URL
            return FileUtil.asInputStream(getFileByURL(new URL(generalPath)));
        } catch (MalformedURLException e) {
            return new FileUtil().asInputStream(generalPath);
        }
    }

    private static File getFileByURL(URL fileUrl) throws FileNotFoundException{
        Validate.notNull(fileUrl, "Resource URL must not be null");

        if (!URL_PROTOCOL_FILE.equals(fileUrl.getProtocol())) {
            throw new FileNotFoundException("URL cannot be resolved to absolute file path " +
                "because it dose not reside in the file system: " + fileUrl);
        }

        try {
            return new File(toURI(fileUrl.toString()).getSchemeSpecificPart());
        } catch (URISyntaxException e) {
            return new File(fileUrl.getFile());
        }
    }

    public static URI toURI(String location) throws URISyntaxException{
        return new URI(StringUtils.replace(location, " ", "%20"));
    }
}