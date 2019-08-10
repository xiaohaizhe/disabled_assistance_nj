package com.hd.home_disabled.timer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.AccessToken;
import com.hd.home_disabled.entity.DingUser;
import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import com.hd.home_disabled.repository.AccessTokenRepository;
import com.hd.home_disabled.repository.DingUserAttendanceRecordRepository;
import com.hd.home_disabled.repository.DingUserRepository;
import com.hd.home_disabled.service.DingUserService;
import com.hd.home_disabled.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName TimeTask
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/5 17:00
 * @Version
 */
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class TimeTask {
    @Value("${ding.get.access.token}")
    private String getAccessToken;//获取accessToken的url
    @Value("${ding.appsecret}")
    private String appsecret;
    @Value("${ding.appkey}")
    private String appkey;

    @Value("${ding.department.list}")
    private String getDepartmentList;
    @Value("${ding.department.userList}")
    private String getDepartmentUserDetail;
    @Value("${ding.attendance.listRecord}")
    private String getAttendanceList;

    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private DingUserService dingUserService;
    @Autowired
    private DingUserRepository dingUserRepository;
    @Autowired
    private DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository;

    private static final Logger logger = LoggerFactory.getLogger(TimeTask.class);

    private final static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行一次
    public void test() {
        System.out.println("当前时间：" + sdf.format(new Date()));
    }

    @Scheduled(cron = "0 0 */1 * * ?") // 每个小时执行一次
    public void getAccessToken() {
        //获取更新access token
        String param1 = "appkey=" + appkey + "&appsecret=" + appsecret;
        JSONObject jsonReturn = HttpUtils.sendGet(getAccessToken, param1);
        Integer errcode = jsonReturn.getInteger("errcode");
        if (errcode == 0) {
            AccessToken accessToken = new AccessToken();
            String access_token = jsonReturn.getString("access_token");
            accessToken.setAccessToken(access_token);
            accessTokenRepository.save(accessToken);
            logger.info("当前时间：" + sdf.format(new Date()));
            logger.info("成功获取token");
        }
        logger.info("当前时间：" + sdf.format(new Date()));
        logger.info("获取token失败");
        //获取更新用户列表
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Sort sort = new Sort(order);
        List<AccessToken> accessTokens = accessTokenRepository.findAll(sort);
        if (accessTokens.size() > 0) {
            List<String> departmentIds = new ArrayList<>();
            String accessToken = accessTokens.get(0).getAccessToken();
            String param2 = "access_token=" + accessToken;
            JSONObject result = HttpUtils.sendGet(getDepartmentList, param2);
            Integer errcode2 = result.getInteger("errcode");
            logger.info("获取部门信息结果:" + errcode2);
            if (errcode2 == 0) {
                logger.info("获取部门信息成功");
                JSONArray array = result.getJSONArray("department");
                for (int i = 1; i < array.size(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    departmentIds.add(object.getString("id"));
                }
            }
            for (String departmentId :
                    departmentIds) {
                String param3 = "access_token=" + accessToken +
                        "&department_id=" + departmentId +
                        "&offset=" + 0 +
                        "&size=" + 100;
                JSONObject result1 = HttpUtils.sendGet(getDepartmentUserDetail, param3);
                Integer errcode1 = result1.getInteger("errcode");
                logger.info("获取部门：" + departmentId + "下用户详情");
                logger.info(result1.toJSONString());
                if (errcode1 == 0) {
                    JSONArray array = result1.getJSONArray("userlist");
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        DingUser user = new DingUser();
                        user.setUserId(object.getString("userid"));
                        user.setDepartmentId(departmentId);
                        user.setMobile(object.getString("mobile"));
                        user.setName(object.getString("name"));
                        dingUserService.save(user);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 */10 * * * ?") // 每10分钟执行一次
    public void getAttendanceRecord() {
        //获取更新用户打卡信息
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Sort sort = new Sort(order);
        List<AccessToken> accessTokens = accessTokenRepository.findAll(sort);
        if (accessTokens.size() > 0) {
            String accessToken = accessTokens.get(0).getAccessToken();
            Date end = new Date();
            Date start = new Date();
            start.setMinutes(start.getMinutes() - 10);
            String url = getAttendanceList+"?access_token=" + accessToken;
            JSONObject params = new JSONObject();
            params.put("checkDateFrom", sdf.format(start));
            params.put("checkDateTo", sdf.format(end));
            params.put("isI18n", false);

            List<DingUser> users= dingUserRepository.findAll();
            String[] userIds = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                userIds[i] = String.valueOf(users.get(i).getUserId());
            }
            params.put("userIds",userIds );
            try{
                JSONObject result = HttpUtils.sendPost(url, params);
                Integer errcode = result.getInteger("errcode");
                if (errcode==0){
                    JSONArray array = result.getJSONArray("recordresult");
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject object = (JSONObject)array.get(i);
                        if (object.getString("checkType")!=null){
                            Date userCheckTime = new Date(object.getLong("userCheckTime"));
                            String dingUserId = object.getString("userId");
                            Byte status = (byte)1;  //项目结束
                            if (object.getString("checkType").equals("OnDuty")){
                                status = (byte)0;   //项目开始
                            }else{
                                System.out.println();
                            }
                            DingUserAttendanceRecord dingUserAttendanceRecord = new DingUserAttendanceRecord();
                            dingUserAttendanceRecord.setDingUserId(dingUserId);
                            dingUserAttendanceRecord.setStatus(status);
                            dingUserAttendanceRecord.setUserCheckTime(userCheckTime);
                            dingUserAttendanceRecordRepository.save(dingUserAttendanceRecord);
                        }
                    }
                }else{
                    logger.error(result.toJSONString());
                }
            }catch (IOException e){
                logger.error(e.getMessage());
            }
        }
    }


}
