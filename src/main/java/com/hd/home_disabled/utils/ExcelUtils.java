package com.hd.home_disabled.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.RESCODE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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

    public static void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        //创建excel工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建第一页
        Sheet sheet = workbook.createSheet("firstSheet");
        //创建第一行
        Row row = sheet.createRow(0);
        //创建第一行上第一个单元格
        Cell cell0 = row.createCell(0);
        Cell cell1 = row.createCell(1);
        Cell cell2 = row.createCell(2);
        Cell cell3 = row.createCell(3);
        Cell cell4 = row.createCell(4);
        Cell cell5 = row.createCell(5);
        Cell cell6 = row.createCell(6);
        //设置第一个单元格内显示
        cell0.setCellValue("姓名");
        cell1.setCellValue("残疾证号");
        cell2.setCellValue("家庭住址");
        cell3.setCellValue("联系电话");
        cell4.setCellValue("托养方式(日托/全托)");
        cell5.setCellValue("补贴金额");
        cell6.setCellValue("托养月数");
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + "model.xls");//Excel文件名
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void exportExcel(String fileName, String[] columnNames, List<JSONArray> jsonArray, HttpServletRequest request, HttpServletResponse response) {
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
            fileName = new String(fileName.getBytes(), "ISO8859-1");
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
    public static JSONObject importExcel(MultipartFile file) {
        if (file.getContentType().equals("application/octet-stream") || file.getContentType().equals("application/vnd.ms-excel")) {
            JSONObject objectReturn = new JSONObject();
            JSONArray array = new JSONArray();
            HSSFWorkbook book;
            try {
                InputStream is = file.getInputStream();
                book = new HSSFWorkbook(is);
                HSSFSheet sheet = book.getSheetAt(0);
                for (int rowNum = 1; rowNum < sheet.getLastRowNum() + 1; rowNum++) {
                    JSONObject object = new JSONObject();
                    HSSFRow row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;//此行为空，进入下一行
                    }
                    //遍历此行的单元格
                    HSSFCell cell0 = row.getCell(0);
                    if (cell0 == null) {
                        continue;//此单元格为空，进入下一单元格
                    }
                    //读取单元格内值
                    String username = readCell(cell0);

                    HSSFCell cell1 = row.getCell(1);
                    if (cell1 == null) {
                        continue;//此单元格为空，进入下一单元格
                    }
                    //读取单元格内值
                    String disabilityCertificateNumber = readCell(cell1);

                    HSSFCell cell2 = row.getCell(2);
                    if (cell2 == null) {
                        continue;//此单元格为空，进入下一单元格
                    }
                    //读取单元格内值
                    String address = readCell(cell2);

                    HSSFCell cell3 = row.getCell(3);
                    if (cell3 == null) {
                        continue;//此单元格为空，进入下一单元格
                    }
                    //读取单元格内值
                    String contactNumber = readCell(cell3);

                    HSSFCell cell4 = row.getCell(4);
                    if (cell4 == null) {
                        continue;//此单元格为空，进入下一单元格
                    }
                    //读取单元格内值
                    String nursingMode = readCell(cell4);

                    HSSFCell cell5 = row.getCell(5);
                    if (cell5 == null) {
                        continue;//此单元格为空，进入下一单元格
                    }
                    //读取单元格内值
                    Float subsidies= Float.parseFloat(readCell(cell5));

                    HSSFCell cell6 = row.getCell(6);
                    if (cell6 == null) {
                        continue;//此单元格为空，进入下一单元格
                    }
                    //读取单元格内值
                    Integer month= Integer.parseInt(readCell(cell6));

                    object.put("username",username);
                    object.put("disabilityCertificateNumber",disabilityCertificateNumber);
                    object.put("address",address);
                    object.put("contactNumber",contactNumber);
                    object.put("nursingMode",nursingMode);
                    object.put("subsidies",subsidies);
                    object.put("month",month);
                    array.add(object);
                }
                logger.debug(array);
                return RESCODE.SUCCESS.getJSONRES(array);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                logger.error(e.getMessage());
                return RESCODE.IO_ERROR.getJSONRES();
            }
        } else {
            return RESCODE.FORMAT_ERROR.getJSONRES();
        }

    }

    /**
     * @param cell
     * @return
     */
    public static String readCell(HSSFCell cell) {
        if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
//            System.out.println("布尔量");
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//            System.out.println("数值型");
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
//                System.out.println("This is date");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                return sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
            } else {
                long value = (long) cell.getNumericCellValue();
//                System.out.println("数值：" + value);
                return String.valueOf(value);
            }
        } else {
//            System.out.println("String型");
            return cell.getStringCellValue();
        }
    }


}
