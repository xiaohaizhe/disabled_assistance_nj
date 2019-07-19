package com.hd.home_disabled.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ExcelUtils
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/10 16:16
 * @Version
 */
@Component
public class ExcelUtils {
    private static Logger logger = LogManager.getLogger(ExcelUtils.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String[] type = new String[]{"java.lang.String", "java.util.Date", "java.lang.Byte",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double"};

    public static void exportExcel(String fileName,String[] columnNames, List<JSONArray> jsonArray, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Excel导出工具");
        //创建excel工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建第一页
        Sheet sheet = workbook.createSheet("firstSheet");
        //创建第一行
        Row row = sheet.createRow(0);
        int row_num = 1;//记录行数
        int colum = 0;//记录列数
        for (String name : columnNames) {
            Cell cell = row.createCell(colum++);
            cell.setCellValue(name);
        }
        System.out.println(jsonArray);
        //一条记录
        for (JSONArray array :
                jsonArray) {
            Row row1 = sheet.createRow(row_num++);
            colum = 0;
            for (Object o : array) {
                JSONObject object = (JSONObject) o;
                for (String key :
                        object.keySet()) {
                    String t = object.get(key).getClass().getName();
                    Cell cell0 = row1.createCell(colum++);
                    switch (t) {
                        case "java.lang.String":
                            cell0.setCellValue(object.get(key).toString());
                            break;
                        case "java.util.Date":
                        case "java.sql.Timestamp":
                            Date date = (Date) object.get(key);
                            String d = sdf.format(date);
                            cell0.setCellValue(d);
                            break;
                        case "java.lang.Byte":
                            cell0.setCellValue((Byte) object.get(key));
                            break;
                        case "java.lang.Short":
                            cell0.setCellValue((Short) object.get(key));
                            break;
                        case "java.lang.Integer":
                            cell0.setCellValue((Integer) object.get(key));
                            break;
                        case "java.lang.Long":
                            cell0.setCellValue((Long) object.get(key));
                            break;
                        case "java.lang.Float":
                            cell0.setCellValue((Float) object.get(key));
                            break;
                        case "java.lang.Double":
                            cell0.setCellValue((Double) object.get(key));
                            break;
                    }
                }
            }
        }
        try {
            fileName = new String(fileName.getBytes(),"ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);//Excel文件名
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
