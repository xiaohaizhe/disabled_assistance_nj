package com.hd.home_disabled.timer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.AccessToken;
import com.hd.home_disabled.entity.DingUser;
import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.repository.AccessTokenRepository;
import com.hd.home_disabled.repository.DingUserAttendanceRecordRepository;
import com.hd.home_disabled.repository.DingUserRepository;
import com.hd.home_disabled.service.DingUserService;
import com.hd.home_disabled.service.UserService;
import com.hd.home_disabled.socket.CheckInWebsocket;
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

import javax.websocket.Session;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

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
    @Autowired
    private UserService userService;
    @Autowired
    private CheckInWebsocket websocket;

    private static final Logger logger = LoggerFactory.getLogger(TimeTask.class);

    private final static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");


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

    @Scheduled(cron = "0 */3 * * * ?") // 每10分钟执行一次
    public void getAttendanceRecord() throws ClassNotFoundException, NoSuchFieldException, IOException {
        //获取更新用户打卡信息
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Sort sort = new Sort(order);
        //1.获得token
        List<AccessToken> accessTokens = accessTokenRepository.findAll(sort);
        if (accessTokens.size() > 0) {
            String accessToken = accessTokens.get(0).getAccessToken();
            Date end = new Date();
            Date start = new Date();
            start.setMinutes(start.getMinutes() - 5);

            String url = getAttendanceList + "?access_token=" + accessToken;
            JSONObject params = new JSONObject();
//            params.put("checkDateFrom", "2019-08-24 11:10:00");
            params.put("checkDateFrom", sdf.format(start));
            params.put("checkDateTo", sdf.format(end));
            params.put("isI18n", false);

            List<DingUser> users = dingUserRepository.findAll();
            List<String> userIdList = new ArrayList<>();
            String[] userIds = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                userIdList.add(String.valueOf(users.get(i).getUserId()));
                userIds[i] = String.valueOf(users.get(i).getUserId());
            }
            int size = users.size();
            if (size > 50) {
                int count = size % 50 == 0 ? size / 50 : (size / 50 + 1);
                for (int j = 0; j < count; j++) {
                    int start_index = j * 50;
                    int end_index = start_index + 50;
                    if (end_index > size) end_index = size;

                    String[] userIdss = getStrings(userIds, start_index, end_index);
                    params.put("userIds", userIdss);
                    saveData(url, params);
                }

            } else {
                params.put("userIds", userIds);
                saveData(url, params);

            }
        }
        CopyOnWriteArraySet<CheckInWebsocket> webSocketSet = (CopyOnWriteArraySet<CheckInWebsocket>) getField(websocket, websocket.getClass(), "webSocketSet");
        for (CheckInWebsocket websocket : webSocketSet) {
            Session session = websocket.getSession();
            List<DingUserAttendanceRecord> dingUserAttendanceRecords = dingUserService.getDingUserAttendanceRecord();
            logger.info("一共有" + dingUserAttendanceRecords.size() + "条数据");
            JSONArray array = new JSONArray();
            for (DingUserAttendanceRecord record :
                    dingUserAttendanceRecords) {
                if (record.getProjectId() == null) {
                    logger.info("根据钉钉用户id：" + record.getDingUserId() + "查找系统用户");
                    if (dingUserService.getUserId(record.getDingUserId()) != null) {
                        User user = dingUserService.getUserId(record.getDingUserId());
                        JSONObject object = new JSONObject();
                        object.put("id", record.getId());
                        object.put("name", user.getName());
                        object.put("disabilityCertificateNumber", user.getDisabilityCertificateNumber());
                        array.add(object);
                    }
                }
            }
            session.getBasicRemote().sendText(array.toJSONString());
        }
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field;
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
            }
        }
        return null;
    }

    private String[] getStrings(String[] s, int start, int end) {
        int size = end - start;
        String[] ss = new String[size];
        int index = 0;
        for (int i = start; i < end; i++) {
            ss[index++] = s[i];
        }
        return ss;
    }

    private void clockIn(String dingUserId, Date userCheckTime) {
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "userCheckTime");
        Sort sort = new Sort(order);
        List<DingUserAttendanceRecord> records = dingUserAttendanceRecordRepository.findByDingUserId(dingUserId, sort);
        if (records.size() > 0
                && records.get(0).getStatus() == 0
                && records.get(0).getProjectId() != null) {
            Integer projectId = records.get(0).getProjectId();
            User user = dingUserService.getUserId(dingUserId);
            Date start = records.get(0).getUserCheckTime();
            if (user != null) {
                userService.clockIn(projectId, user.getId(), start, userCheckTime, 1);
            }
        }
    }

    private void saveData(String url, JSONObject params) {
        try {
            JSONObject result = HttpUtils.sendPost(url, params);
            Integer errcode = result.getInteger("errcode");
            if (errcode == 0) {
                JSONArray array = result.getJSONArray("recordresult");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    logger.info("钉钉接口返回打卡数据：");
                    logger.info(object.toJSONString());
                    if (object.getString("checkType") != null) {
                        Date userCheckTime = new Date(object.getLong("userCheckTime"));
                        String dingUserId = object.getString("userId");
                        Byte status = (byte) 1;  //项目结束
                        if (object.getString("checkType").equals("OnDuty")) {
                            status = (byte) 0;   //项目开始
                        }
                        if (status == (byte) 1) {
                            //残疾人参加项目打卡结束,参与项目时间统计录入数据库
                            Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "userCheckTime");
                            Sort sort1 = new Sort(order1);
                            List<DingUserAttendanceRecord> dingUserAttendanceRecords = dingUserAttendanceRecordRepository.findByDingUserId(dingUserId, sort1);
                            logger.info(dingUserAttendanceRecords.toString());
                            if (dingUserAttendanceRecords.size() > 0
                                    && dingUserAttendanceRecords.get(0).getStatus() == (byte) 0
                                    && dingUserAttendanceRecords.get(0).getProjectId() != null) {
                                User user = dingUserService.getUserId(dingUserId);
                                if (user != null) {
                                    userService.clockIn(dingUserAttendanceRecords.get(0).getProjectId(), user.getId(),
                                            dingUserAttendanceRecords.get(0).getUserCheckTime(), userCheckTime, 1);
                                }
                            }
                        }
                        DingUserAttendanceRecord dingUserAttendanceRecord = new DingUserAttendanceRecord();
                        dingUserAttendanceRecord.setDingUserId(dingUserId);
                        dingUserAttendanceRecord.setStatus(status);
                        dingUserAttendanceRecord.setUserCheckTime(userCheckTime);
                        logger.info(dingUserAttendanceRecord.toString());
                        Optional<DingUserAttendanceRecord> dingUserAttendanceRecordOptional = dingUserAttendanceRecordRepository.findByDingUserIdAnAndUserCheckTime(dingUserId, userCheckTime);
                        if (!dingUserAttendanceRecordOptional.isPresent())
                            dingUserAttendanceRecordRepository.save(dingUserAttendanceRecord);

                    }
                }
            } else {
                logger.error(result.toJSONString());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
