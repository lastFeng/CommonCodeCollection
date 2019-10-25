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

import com.google.common.collect.Lists;
import com.learn.excel.annotation.ExcelField;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Title: </p>
 *
 * <p> Description: </p>
 *
 * @author: Guo Weifeng
 * @version: 1.0
 * @create: 2019/10/23 14:40
 * 导出Excel文件（导出“XLSX”格式，支持大数据导出）
 */
public class ExportExcel {
    /**
     * 工作簿对象
     * */
    private SXSSFWorkbook wb;

    /**
     * 工作表对象
     * */
    private Sheet sheet;

    /**
     * 样式列表
     * */
    private Map<String, CellStyle> styles;

    /**
     * 当前行号
     * */
    private int rownum;

    /**
     * 注解列表
     * */
    List<Object[]> annotationList = Lists.newArrayList();

    /**
     * @param title 表格标题，传“空值”表示无标题
     * @param cla 实体对象，通过annotation.ExportField获取标题
     * */
    public ExportExcel(String title, Class<?> cla) {
        this(title, cla, 1);
    }

    /**
     * @param title
     * @param cla
     * @param type 导出导入类型
     * @param groups 导入分组
     * @since 1.8
     * */
    public ExportExcel(String title, Class<?> cla, int type, int... groups) {

        // get annotation fields
        Field[] fs = cla.getDeclaredFields();
        for (Field f: fs) {
            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type() == 0 || ef.type() == type)) {
                if (groups != null && groups.length > 0) {
                    boolean inGroup = false;

                    for (int g: groups) {
                        if (inGroup) {
                            break;
                        }
                        for (int efg: ef.groups()) {
                            if (g == efg) {
                                inGroup = true;
                                annotationList.add(new Object[]{ef, f});
                                break;
                            }
                        }
                    }
                } else {
                    annotationList.add(new Object[]{ef, f});
                }
            }
        }

        // get annotation method
        Method[] ms = cla.getDeclaredMethods();
        for (Method m: ms) {
            ExcelField ef = m.getAnnotation(ExcelField.class);
            if (ef != null && (ef.type() == 0 || ef.type() == type)) {
                if (groups != null && groups.length > 0) {
                    boolean inGroup = false;

                    for (int g: groups) {
                        if (inGroup){
                            break;
                        }
                        for (int efg: ef.groups()) {
                            if (g == efg) {
                                inGroup = true;
                                annotationList.add(new Object[]{ef, m});
                                break;
                            }
                        }
                    }
                } else {
                    annotationList.add(new Object[]{ef, m});
                }
            }
        }

        // Field sorting
        Collections.sort(annotationList, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                return new Integer(((ExcelField)o1[0]).sort()).compareTo(
                    new Integer(((ExcelField)o2[0]).sort())
                );
            }
        });

        // initialize
        List<String> headerList = Lists.newArrayList();
        for (Object[] os: annotationList) {
            String t = ((ExcelField)os[0]).title();
            // 如果是导出的话，则去掉注释
            if (type == 1) {
                String[] ss = StringUtils.split(t, "**", 2);
                if (ss.length == 2) {
                    t = ss[0];
                }
            }
            headerList.add(t);
        }
        initialize(title, headerList);
    }

    public ExportExcel(String title, String[] headers) {
        initialize(title, Lists.newArrayList(headers));
    }

    public ExportExcel(String title, List<String> headerList) {
        initialize(title, headerList);
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    /**
     * 初始化函数
     * @param title 表格标题
     * @param headerList 表头列表
     * */
    private void initialize(String title, List<String> headerList) {
        this.wb = new SXSSFWorkbook(500);
        this.sheet = wb.createSheet("Export");
        this.styles = createStyles(wb);

        // create title
        if (StringUtils.isNotBlank(title)) {
            Row titleRow = sheet.createRow(rownum++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            // 合并单元格
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(),
                titleRow.getRowNum(), headerList.size() - 1));
        }

        // create header
        if (headerList == null) {
            throw new RuntimeException("headerList not null!");
        }

        Row headerRow = sheet.createRow(rownum++);
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String[] ss = StringUtils.split(headerList.get(i), "**", 2);

            if (ss.length == 2) {
                cell.setCellValue(ss[0]);
                Comment comment = this.sheet.createDrawingPatriarch().createCellComment(
                    new XSSFClientAnchor(0, 0, 0, 0, (short)3, 3, (short)5, 6));
                comment.setString(new XSSFRichTextString(ss[1]));
                cell.setCellComment(comment);
            } else {
                cell.setCellValue(headerList.get(i));
            }
        }

        // 设置cell的宽度
        for (int i = 0; i < headerList.size(); i++) {
            int colWidth = sheet.getColumnWidth(i) * 2;
            sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
        }
    }

    private Map<String, CellStyle> createStyles(SXSSFWorkbook wb) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

        // title style
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short)16);
        titleFont.setBold(true);
        style.setFont(titleFont);
        styles.put("title", style);

        // data style
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short)10);
        style.setFont(dataFont);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.LEFT);
        styles.put("data1", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        styles.put("data2", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        styles.put("data3", style);

        // header style
        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short)10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }

    public Row addRow() {
        return sheet.createRow(rownum++);
    }

    public Cell addCell(Row row, int column, Object val) {
        return this.addCell(row, column, val, 0, Class.class);
    }

    /**
     * @param row 添加的行
     * @param column 添加列号
     * @param val 添加值
     * @param align 对齐方式（1：靠左；2：居中；3：靠右）
     * @param fieldType 单元格对象
     * */
    public Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType) {
        Cell cell = row.createCell(column);
        String cellFormatString = "@";

        try {
            if (val == null) {
                cell.setCellValue("");
            } else if (fieldType != Class.class) {
                cell.setCellValue((String)fieldType.getMethod("setValue", Object.class).invoke(null, val));
            } else {
                if (val instanceof String) {
                    cell.setCellValue((String) val);
                } else if (val instanceof Integer) {
                    cell.setCellValue((Integer) val);
                    cellFormatString = "0";
                } else if (val instanceof Long) {
                    cell.setCellValue((Long) val);
                    cellFormatString = "0";
                } else if (val instanceof Double) {
                    cell.setCellValue((Double) val);
                    cellFormatString = "0.00";
                } else if (val instanceof Float) {
                    cell.setCellValue((Float) val);
                    cellFormatString = "0.00";
                } else if (val instanceof Date) {
                    cell.setCellValue((Date) val);
                    cellFormatString = "yyyy-MM-dd HH:mm";
                } else {
                    cell.setCellValue((String)Class.forName(this.getClass().getName().replaceAll(
                        this.getClass().getSimpleName(), "fieldtype." + val.getClass().getSimpleName() + "Type"
                    )).getMethod("setValue", Object.class).invoke(null, val));
                }
            }

            if (val != null) {
                CellStyle style = styles.get("data_column" + column);
                if (style == null) {
                    style = wb.createCellStyle();
                    style.cloneStyleFrom(styles.get("data" + (align >= 1 && align <=3 ? align : "")));
                    style.setDataFormat(wb.createDataFormat().getFormat(cellFormatString));
                    styles.put("data_column" + column, style);
                }
                cell.setCellStyle(style);
            }
        } catch (Exception e) {
            cell.setCellValue(String.valueOf(val));
        }
        return cell;
    }

    /**
     * 添加数据
     * */
    public <E> ExportExcel setDataList(List<E> list) {
        for (E e: list) {
            int colunm = 0;
            Row row = this.addRow();
            StringBuilder sb = new StringBuilder();
            for (Object[] os: annotationList) {
                ExcelField ef = (ExcelField) os[0];
                Object val = null;

                // get entity value
                try {
                    if (StringUtils.isNotBlank(ef.value())) {
                        val = Reflections.invokeGetter(e, ef.value());
                    } else {
                        if (os[1] instanceof Field) {
                            val = Reflections.invokeGetter(e, ((Field)os[1]).getName());
                        } else if (os[1] instanceof Method) {
                            val = Reflections.invokeMethod(e, ((Method)os[1]).getName(), new Class[]{}, new Object[]{});
                        }
                    }

                    // if is dict, get dict label
                    if (StringUtils.isNotBlank(ef.dictType())) {
                        // TODO
                    }
                } catch (Exception ex) {
                    val = "";
                }
                this.addCell(row, colunm++, val, ef.algin(), ef.fieldType());
                sb.append(val + ", ");
            }
        }
        return this;
    }

    /**
     * 输出数据流
     * */
    public ExportExcel write(OutputStream outputStream) throws IOException {
        wb.write(outputStream);
        return this;
    }
    /**
     * 输出到客户端
     * */
    public ExportExcel write(HttpServletResponse response, String fileName) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
        write(response.getOutputStream());
        return this;
    }

    /**
     * 输出到文件
     * */
    public ExportExcel writeFile(String name) throws FileNotFoundException, IOException {
        FileOutputStream os = new FileOutputStream(name);
        this.write(os);
        return this;
    }

    /**
     * 从浏览器中下载
     * */
    public ExportExcel writeBrowser(ServletOutputStream servletOutputStream) throws IOException {
        this.write(servletOutputStream);
        return this;
    }

    /**
     * 清理临时文件
     * */
    public ExportExcel dispose() {
        wb.dispose();
        return this;
    }

    /**
     * 导出测试
     * */
    public static void main(String[] args) throws Exception {
        List<String> headerList = Lists.newArrayList();
        for (int i = 1; i <= 10; i++) {
            headerList.add("表头" + i);
        }

        List<String> dataRowList = Lists.newArrayList();
        for (int i = 1; i <= headerList.size(); i++) {
            dataRowList.add("数据" + i);
        }

        List<List<String>> dataList = Lists.newArrayList();
        for (int i = 1; i <= 100000; i++) {
            dataList.add(dataRowList);
        }

        ExportExcel ee = new ExportExcel("表格标题", headerList);

        for (int i = 0; i < dataRowList.size(); i++) {
            Row row = ee.addRow();
            for (int j = 0; j < dataList.get(i).size(); j++) {
                ee.addCell(row, j, dataList.get(i).get(j));
            }
        }

        ee.writeFile("target/export.xlsx");
        ee.dispose();

        System.out.println("Export success");
    }
}