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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/24 10:49
 */
public class Encodes {

    private static final String DEFAULT_URL_ENCODING = "UTF-8";
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();


    /**
     * Hex编码
     * */
    public static String encodeHex(byte[] input) {
        return new String(Hex.encodeHex(input));
    }

    /**
     * Hex解码
     * */
    public static byte[] decodeHex(String input) {
        try {
            return Hex.decodeHex(input);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Base64
     * */
    public static String encodeBase64(byte[] input) {
        return new String(Base64.encodeBase64(input));
    }

    public static String encodeBase64(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }

        try {
            return new String(Base64.encodeBase64(input.getBytes(DEFAULT_URL_ENCODING)));
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static byte[] encodeUrlSafeBase64(byte[] input) {
        return Base64.encodeBase64URLSafe(input);
    }

    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input.getBytes());
    }

    public static String decodeBase64String(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }

        try {
            return new String(Base64.decodeBase64(input.getBytes(DEFAULT_URL_ENCODING)));
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String encodeBase62(byte[] input) {
        char[] chars = new char[input.length];
        for (int i = 0; i < input.length; i++) {
            chars[i] = BASE62[((input[i] & 0xFF) % BASE62.length)];
        }
        return new String(chars);
    }

    /**
     * Html转码
     * */
    public static String escapeHtml(String html) {
        return StringEscapeUtils.escapeHtml4(html);
    }

    public static String unescapeHtml(String htmlEscape) {
        return StringEscapeUtils.unescapeHtml4(htmlEscape);
    }

    /**
     * XML
     * */
    public static String escapeXML(String xml) {
        return StringEscapeUtils.escapeXml10(xml);
    }

    public static String unEscapeXML(String escapeXml) {
        return StringEscapeUtils.unescapeXml(escapeXml);
    }

    /**
     * URL编码，Encode默认为UTF-8
     * */
    public static String urlEncode(String part) {
        try {
            return URLEncoder.encode(part, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String urlDecode(String part) {
        try {
            return URLDecoder.decode(part, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}