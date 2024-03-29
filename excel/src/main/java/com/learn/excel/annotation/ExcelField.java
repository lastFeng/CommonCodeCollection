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
package com.learn.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 14:30
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {
    /**
     * 导出字段名（默认调用当前字段的“get”方法，如指定导出字段为对象，请填写“对象名.对象属性”）
     * */
    String value() default "";

    /**
     * 导出字段标题（需要添加批注，请用“**”分隔，标题**批注，进队导出模板有效）
     * */
    String title();

    /**
     * 字段类型（0：导出导入，1：仅导出，2：仅导入）
     * */
    int type() default 0;

    /**
     * 导出字段对其方式（0：自动；1：靠左；2：居中；3：靠右）
     * */
    int algin() default 0;

    /**
     * 导出字段排序（升序）
     * */
    int sort() default 0;

    /**
     * 如果是字典类型，请设置字典的type值
     * */
    String dictType() default "";

    /**
     * 反射类型
     * */
    Class<?> fieldType() default Class.class;

    /**
     * 字段归属组（根据分组导出导入）
     * */
    int[] groups() default {};


}