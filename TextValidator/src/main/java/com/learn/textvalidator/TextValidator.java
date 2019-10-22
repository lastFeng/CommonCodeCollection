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
package com.learn.textvalidator;

import com.sun.istack.internal.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/22 14:16
 *
 * 通过正则表达式判断是否正确的手机号、固定电话、身份证、邮箱等
 *
 * 从AndroidUtilCode的RegexUtils移植，性能优化将正则表达式为预编译，并修改了TEL的正则表达式
 */
public class TextValidator {
    /**
     * 正则：手机号（简单），1字头+10位数字即可
     * */
    private static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
    private static final Pattern PATTERN_REGEX_MOBILE_SIMPLE = Pattern.compile(REGEX_MOBILE_SIMPLE);

    /**
     * 正则：手机号（精确），已知3位前缀+8位数字
     * <p>
     *     移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
     * </p>
     * <p>
     *     联通：130、131、132、145、155、156、175、176、185、186
     * </p>
     * <p>
     *     电信：133、153、173、177、180、181、189
     * </p>
     * <p>
     *     全球星：1349
     * </p>
     * <p>
     *     虚拟运营商：170
     * </p>
     * */
    private static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";
    private static final Pattern PATTERN_REGEX_MOBILE_EXACT = Pattern.compile(REGEX_MOBILE_EXACT);

    /**
     * 正则：固定电话号码，可带区号，然后6至少8位数字
     * */
    private static final String REGEX_TEL = "^(\\d{3,4}-?\\d{6,8})";
    private static final Pattern PATTERN_REGEX_TEL = Pattern.compile(REGEX_TEL);

    /**
     * 正则：身份证号码15位，数字且关于生日的部分必须正确
     * */
    private static final String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    private static final Pattern PATTERN_REGEX_ID_CARD15 = Pattern.compile(REGEX_ID_CARD15);

    /**
     * 正则：身份证号码18位，数字且关于生日的部分必须正确
     * */
    private static final String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    private static final Pattern PATTERN_REGEX_ID_CARD18 = Pattern.compile(REGEX_ID_CARD18);

    /**
     * 正则：邮箱，有效字符（不支持中文），中间必须有@，后半部分必须有.
     * */
    private static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    private static final Pattern PATTERN_REGEX_EMAIL = Pattern.compile(REGEX_EMAIL);

    /**
     * 正则：URL,必须有“://”，前面必须是英文，后面不能有空格
     * */
    private static final String REGEX_ULR = "[a-zA-Z]+://[^\\s]*";
    private static final Pattern PATTERN_REGEX_URL = Pattern.compile(REGEX_ULR);

    /**
     * 正则：IP地址（ipv4）
     * */
    private static final String REGEX_IPV4 = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    private static final Pattern PATTERN_REGEX_IPV4 = Pattern.compile(REGEX_IPV4);

    /**
     * 验证手机号（简单）
     */
    public static boolean isMobileSimple(@Nullable CharSequence input) {
        return isMatch(PATTERN_REGEX_MOBILE_SIMPLE, input);
    }

    /**
     * 验证手机号（精确）
     * */
    public static boolean isMobileExact(@Nullable CharSequence input) {
        return isMatch(PATTERN_REGEX_MOBILE_EXACT, input);
    }

    /**
     * 验证固定电话号码
     * */
    public static boolean isTel(@Nullable CharSequence input) {
        return isMatch(PATTERN_REGEX_TEL, input);
    }

    /**
     * 验证15或18位身份证号码
     * */
    public static boolean isIdCard(@Nullable CharSequence input) {
        return isMatch(PATTERN_REGEX_ID_CARD15, input) || isMatch(PATTERN_REGEX_ID_CARD18, input);
    }

    /**
     * 验证邮箱
     * */
    public static boolean isEmail(@Nullable CharSequence input) {
        return isMatch(PATTERN_REGEX_EMAIL, input);
    }

    /**
     * 验证URL
     */
    public static boolean isUrl(@Nullable CharSequence input) {
        return isMatch(PATTERN_REGEX_URL, input);
    }


    /**
     * 验证IPv4
     */
    public static boolean isIpv4(@Nullable CharSequence input) {
        return isMatch(PATTERN_REGEX_IPV4, input);
    }

    public static boolean isMatch(Pattern pattern, CharSequence input) {
        return StringUtils.isNotEmpty(input) && pattern.matcher(input).matches();
    }
}