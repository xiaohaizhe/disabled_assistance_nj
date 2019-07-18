package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.service.ApplyFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

/**
 * @ClassName UploadController
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/10 11:48
 * @Version
 */
@RestController
@RequestMapping("/api/file")
public class FileController {
    private final Environment environment;

    public FileController(Environment environment) {
        this.environment = environment;
    }
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    /**
     * 上传文件
     * @param file 文件
     * @param type
     * 文件类型：
     * 机构：
     * certification-机构营业执照或登记证书
     * openBankAccountPermitCertificate-银行开户许可
     * staffList-专职工作人员名单
     * managementSystem-管理制度
     * facilitiesPictures-机构设施图片：门头及室内功能区域、无障碍设施
     * 项目：
     * projectImage-项目图片
     * nursingList-托养残疾人名单
     * lowIncomeCertificate-低保或其他低收入证明
     * @return 结果
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject uploadImages(@RequestParam(value = "file") MultipartFile file, String type) {
        if (file.isEmpty()) return RESCODE.FAILURE.getJSONRES("文件不存在");
        String fileName = file.getOriginalFilename();  // 文件名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名
        String filePath = System.getProperty("user.dir")+"\\picture"+"\\" + type + "\\"; // 上传后的路径,即本地磁盘
        fileName = UUID.randomUUID() + suffixName; // 新文件名
        File dest = new File(filePath + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return RESCODE.FAILURE.getJSONRES(e.getMessage());
        }
        String filename = "/picture/" + type + "/" + fileName;//本地目录和生成的文件名拼接，这一段存入数据库
        String fileUrl = "http://"+getHostIp()+":"+getPort()+filename;
        JSONObject url = new JSONObject();
        url.put("fileUrl",fileUrl);
        return RESCODE.SUCCESS.getJSONRES(url);
    }



    private String getPort(){
        return environment.getProperty("local.server.port");
    }
    private String getHostIp(){
        InetAddress localHost;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return null;
        }
        return localHost.getHostAddress();  // 返回格式为：xxx.xxx.xxx
    }
}
