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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public static void exportExcel(String[] columnNames, List<JSONObject> jsonArray, HttpServletRequest request, HttpServletResponse response) {
        //创建excel工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建第一页
        Sheet sheet = workbook.createSheet("firstSheet");
        //创建第一行
        Row row = sheet.createRow(0);
        int row_num = 1;
        int colum = 0;//记录列数
        for (String name : columnNames){
            Cell cell = row.createCell(colum++);
            cell.setCellValue(name);
        }
        for (JSONObject object:
                jsonArray) {
            Row row1 = sheet.createRow(row_num++);
            Cell cell0 = row1.createCell(0);
            logger.info("表格数据++++++++++++++++++++++++++++++++++++");
            logger.info("index:"+object.get("index"));
            cell0.setCellValue(Integer.valueOf(object.get("index").toString()));
            Cell cell1 = row1.createCell(1);
            logger.info("name:"+object.get("name"));
            cell1.setCellValue(object.get("name").toString());
            Cell cell2 = row1.createCell(2);
            cell2.setCellValue((Date) object.get("registrationTime"));
            Cell cell3 = row1.createCell(3);
            cell3.setCellValue(object.get("registrationCertificateNumber").toString());
            Cell cell4 = row1.createCell(4);
            cell4.setCellValue(object.get("registrationDepartment").toString());
            Cell cell5 = row1.createCell(5);
            cell5.setCellValue(object.get("nature").toString());
            Cell cell6 = row1.createCell(6);
            cell6.setCellValue(object.get("natureOfHousingPropertyRight").toString());
            Cell cell7 = row1.createCell(7);
            cell7.setCellValue(Float.valueOf(object.get("area").toString()));
            Cell cell8 = row1.createCell(8);
            cell8.setCellValue(Integer.valueOf(object.get("bedNum").toString()));
            Cell cell9 = row1.createCell(9);
            cell9.setCellValue(object.get("asylumLaborProjects").toString());
            Cell cell10 = row1.createCell(10);
            cell0.setCellValue(object.get("detailedAddress").toString());
            Cell cell11 = row1.createCell(11);
            cell11.setCellValue(object.get("personInCharge").toString());
            Cell cell12 = row1.createCell(12);
            cell12.setCellValue(object.get("gender").toString());
            Cell cell13 = row1.createCell(13);
            cell13.setCellValue(object.get("birthMonth").toString());
            Cell cell14 = row1.createCell(14);
            cell14.setCellValue(object.get("education").toString());
            Cell cell15 = row1.createCell(15);
            cell15.setCellValue(object.get("certification").toString());
            Cell cell16 = row1.createCell(16);
            cell16.setCellValue(object.get("openBankAccountPermitCertificate").toString());
            Cell cell17 = row1.createCell(17);
            cell17.setCellValue(object.get("facilitiesPictures").toString());
            Cell cell18 = row1.createCell(18);
            cell18.setCellValue(object.get("staffList").toString());
            Cell cell19 = row1.createCell(19);
            cell19.setCellValue(object.get("managementSystem").toString());
            Cell cell20 = row1.createCell(20);
            cell20.setCellValue(Integer.valueOf(object.get("projectSum").toString()));
            Cell cell21 = row1.createCell(21);
            cell21.setCellValue(Integer.valueOf(object.get("personCountSum").toString()));
            Cell cell22 = row1.createCell(22);
            cell22.setCellValue(Integer.valueOf(object.get("personTimeSum").toString()));
            Cell cell23 = row1.createCell(23);
            cell23.setCellValue(Integer.valueOf(object.get("totalTimeSum").toString()));
            Cell cell24 = row1.createCell(24);
            cell24.setCellValue(Float.valueOf(object.get("averageTime").toString()));
            Cell cell25 = row1.createCell(25);
            cell25.setCellValue(object.get("adminName").toString());
            Cell cell26 = row1.createCell(26);
            cell26.setCellValue((Date)object.get("createTime"));
            Cell cell27 = row1.createCell(27);
            cell27.setCellValue((Date)object.get("lastModifyTime"));
        }
        /*Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        //创建第一行上第一个单元格
        Cell cell0 = row.createCell(0);
        Cell cell1 = row.createCell(1);
        Cell cell2 = row.createCell(2);*/
        //设置第一个单元格内显示
        /*cell0.setCellValue("index");
        cell1.setCellValue("设备名称");
        cell2.setCellValue("设备鉴权信息");
        Cell cell10 = row1.createCell(0);
        Cell cell11 = row1.createCell(1);
        Cell cell12 = row1.createCell(2);
        cell10.setCellValue(1);
        cell11.setCellValue("test1");
        cell12.setCellValue("3264XXX83");
        Cell cell20 = row2.createCell(0);
        Cell cell21 = row2.createCell(1);
        Cell cell22 = row2.createCell(2);
        cell20.setCellValue(2);
        cell21.setCellValue("test2");
        cell22.setCellValue("3264XXX84");*/
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename="+"cell_link_device_.xls");//Excel文件名
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
