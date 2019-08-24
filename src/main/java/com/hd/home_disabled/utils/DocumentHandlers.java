package com.hd.home_disabled.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentHandlers {
    private Configuration configuration = null;
    public DocumentHandlers() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");//设置默认编码方式
    }
    /**
     * !注意dataMap里存放的数据Key值要与模板中的参数相对应,图片为String类型的Base64编码
     * @param dataMap 载入的数据文件
     * @param templatePackagePath 模版文件包路径
     * @param templateName 模版文件名称
     */
    public void createDoc(Map<String, Object> dataMap, String templateName, String orgName) throws IOException {
        // 设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
//      // 这里我们的模板是放在com.havenliu.document.template包下面
        String dic=System.getProperty("user.dir");
        String path=dic+"/src/main/resources/static/template";
        String docPath=path+"/"+orgName+".doc";

        configuration.setDirectoryForTemplateLoading(new File(path));
        Template t = null;
        try {
            t = configuration.getTemplate(templateName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 输出文档路径及名称
        File outFile = new File(docPath);
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outFile),"utf-8"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            t.process(dataMap, out);
            out.flush();
            out.close();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 测试
     */
  public static void main(String[] args) throws Exception {
      Map<String, Object> dataMap=new HashMap<String, Object>();
      String img=null;
      InputStream in;
      byte[] picdata=null;
      try {
          in=new FileInputStream("/Users/sunyuan/develop/privateProject/disabled_assistance_nj/src/main/resources/static/images/ewm.png");
          picdata=new byte[in.available()];
          in.read(picdata);
          in.close();
      } catch (Exception e) {
          e.printStackTrace();
      }
      BASE64Encoder encoder=new BASE64Encoder();
      img=encoder.encode(picdata);
      //基本信息
       dataMap.put("name", "张三");
       dataMap.put("city", "男");
       dataMap.put("age", "20");
       dataMap.put("time", "1988");
       dataMap.put("person", "9");
       dataMap.put("number", "本科");
       dataMap.put("area", "未婚");
       dataMap.put("bed", "7年工作经验"); //工作经验
       dataMap.put("nature", "陕西-西安");
       dataMap.put("labor", "123456@126.com");
       dataMap.put("hkaddress", "陕西-西安"); //户口所在地
       dataMap.put("studyabord", "无"); //海外学习经历
       dataMap.put("phone", "12345678912");
      List<String> images = new ArrayList<String>();
      images.add(img);
      images.add(img);
      images.add(img);
      images.add(img);
      dataMap.put("images", images);

      // 求职意向
       dataMap.put("jobintention", "全职");
       dataMap.put("salary", "1000元/月"); // 期望薪资
       dataMap.put("workaddress", "陕西-西安"); //期望工作地点
       dataMap.put("workstatus", "我目前处于离职状态，可立即上岗"); //目前工作状态
       //工作经历
       List<Map<String,Object>> wlist = new ArrayList<Map<String,Object>>();
       for(int i=0;i<4;i++){
             Map<String,Object> map = new HashMap<String,Object>();
             map.put("no", "2010/7"); //开始时间
             map.put("name", "2017/7"); //结束时间
             map.put("id", "陕西中建网络科技有限公司"+i); //公司名称
           wlist.add(map);
       }
       dataMap.put("list", wlist);
       //项目经验
       List<Map<String,Object>> plist = new ArrayList<Map<String,Object>>();
       for(int i=0;i<4;i++){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("starttime", "2010/7"); //开始时间
            map.put("endtime", "2017/7"); //结束时间
            map.put("name", "中国建设产业网"+i);//项目名称
            map.put("duty", "程序员");//担任职责
            map.put("describe", "项目描述项目描述项目描述项目描述项目描述项目描述项目描述项目描述项目描述项目描述项目描述项目描述项目描述项目描述");//项目描述
            plist.add(map);
       }
       dataMap.put("plist", plist);
       //教育背景
       List<Map<String,Object>> elist = new ArrayList<Map<String,Object>>();
       for(int i=0;i<4;i++){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("starttime", "2010/7"); //开始时间
            map.put("endtime", "2017/7"); //结束时间
            map.put("school", "西安交通大学"+i);//毕业学校
            map.put("major", "计算机");//所学专业
            map.put("education", "本科");//获得学历
            elist.add(map);

       }
       dataMap.put("elist", elist);
      //证书
       List<Map<String,Object>> clist = new ArrayList<Map<String,Object>>();
       for(int i=0;i<2;i++){
           Map<String,Object> map = new HashMap<String,Object>();
           map.put("cname", "计算机三级证");//证书名称
           map.put("year", "2014");//获得时间-年
           map.put("month", "8");//获得时间-月
           clist.add(map);
       }
       dataMap.put("clist", clist);
      //自我评价
       dataMap.put("selfevaluation", "1.自我评价自我评价自我评价自我评价自我评价2.自我评价自我评价自我评价自我评价3.自我评价自我评价自我评价自我评价自我评价");
      DocumentHandlers documentHandler=new DocumentHandlers();
      documentHandler.createDoc(dataMap,"Doc5.ftl", "/Users/sunyuan/develop/out2.doc");
  }
    public static String getImageBase(String src) throws Exception {
            if (src == null || src == "") {
                    return "";
                }
            File file = new File(src);
            if (!file.exists()) {
                    return "";
                }
            InputStream in = null;
            byte[] data = null;
            try {
                    in = new FileInputStream(file);
                    data = new byte[in.available()];
                    in.read(data);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            BASE64Encoder encoder = new BASE64Encoder();
             return encoder.encode(data);
         }

}