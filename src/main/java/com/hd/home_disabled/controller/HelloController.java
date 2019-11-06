package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSON;
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
import com.hd.home_disabled.timer.TimeTask;
import com.hd.home_disabled.utils.HttpUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author pyt
 * @Date 2019/6/25 14:11
 * @Version
 */
@RestController
@RequestMapping("/")
public class HelloController {
    @Value("${ding.get.access.token}")
    private String getAccessToken;
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

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

/*    @ApiOperation(value = "测试", notes = "测试")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() throws IOException {

//        getField()
        return "Hello!";
    }*/


    @RequestMapping(value = "/updateToken", method = RequestMethod.GET)
    public String testAccessToken() {
        String param = "appkey=" + appkey + "&appsecret=" + appsecret;
        JSONObject jsonReturn = HttpUtils.sendGet(getAccessToken, param);
        Integer errcode = jsonReturn.getInteger("errcode");
        if (errcode == 0) {
            AccessToken accessToken = new AccessToken();
            String access_token = jsonReturn.getString("access_token");
            accessToken.setAccessToken(access_token);
            Optional<AccessToken> optionalAccessToken = accessTokenRepository.findByAccessToken(access_token);
            if (!optionalAccessToken.isPresent()) accessTokenRepository.save(accessToken);
            return access_token;
        }
        return "失败";
    }

    @RequestMapping(value = "/getDepartmentList", method = RequestMethod.GET)
    public void getDepartmentList() {
        try {
            InetAddress ip4 = Inet4Address.getLocalHost();
            System.out.println(ip4.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //1.获取token调用接口
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Sort sort = new Sort(order);
        List<AccessToken> accessTokens = accessTokenRepository.findAll(sort);
        if (accessTokens.size() > 0) {
            String accessToken = accessTokens.get(0).getAccessToken();
            Date end = new Date();
            Date start = new Date();
            start.setMinutes(start.getMinutes() - 10);

            String url = getAttendanceList + "?access_token=" + accessToken;
            JSONObject params = new JSONObject();
            params.put("checkDateFrom", "2019-08-24 11:10:00");
//            params.put("checkDateFrom", sdf.format(start));
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
                    try {
                        JSONObject result = HttpUtils.sendPost(url, params);
                        Integer errcode = result.getInteger("errcode");
                        if (errcode == 0) {
                            JSONArray array = result.getJSONArray("recordresult");
                            for (int i = 0; i < array.size(); i++) {
                                JSONObject object = (JSONObject) array.get(i);
                                logger.info(object.toJSONString());
                               /* if (object.getString("checkType") != null) {
                                    Date userCheckTime = new Date(object.getLong("userCheckTime"));
                                    String dingUserId = object.getString("userId");
                                    Byte status = (byte) 1;  //项目结束
                                    if (object.getString("checkType").equals("OnDuty")) {
                                        status = (byte) 0;   //项目开始
                                    }
                                    if (status == (byte) 1) {
                                        //残疾人参加项目打卡结束,参与项目时间统计录入数据库
                                       *//* Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "userCheckTime");
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
                                        }*//*
                                    }
                                    DingUserAttendanceRecord dingUserAttendanceRecord = new DingUserAttendanceRecord();
                                    dingUserAttendanceRecord.setDingUserId(dingUserId);
                                    dingUserAttendanceRecord.setStatus(status);
                                    dingUserAttendanceRecord.setUserCheckTime(userCheckTime);
                                    logger.info(dingUserAttendanceRecord.toString());
                                    Optional<DingUserAttendanceRecord> dingUserAttendanceRecordOptional = dingUserAttendanceRecordRepository.findByDingUserIdAnAndUserCheckTime(dingUserId, userCheckTime);
                                    if (!dingUserAttendanceRecordOptional.isPresent())
                                        dingUserAttendanceRecordRepository.save(dingUserAttendanceRecord);

                                }*/
                            }
                        } else {
                            logger.error(result.toJSONString());
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                }

            } else {
                params.put("userIds", userIds);
                try {
                    JSONObject result = HttpUtils.sendPost(url, params);
                    Integer errcode = result.getInteger("errcode");
                    if (errcode == 0) {
                        JSONArray array = result.getJSONArray("recordresult");
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject object = (JSONObject) array.get(i);
                            logger.info(object.toJSONString());
                            /*if (object.getString("checkType") != null) {
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

                            }*/
                        }
                    } else {
                        logger.error(result.toJSONString());
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }

            }
        }
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

/*    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    public void getUserList(){
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
                        System.out.println(object.get("userid"));
                        System.out.println(object.getString("userid"));
                        user.setUserId(object.getString("userid"));
                        user.setDepartmentId(departmentId);
                        user.setMobile(object.getString("mobile"));
                        user.setName(object.getString("name"));
                        dingUserService.save(user);
                    }
                }
            }
        }
    }*/

    @RequestMapping(value = "/getRecord", method = RequestMethod.GET)
    public void getUserRecord() {
        for (DingUserAttendanceRecord record : dingUserService.getDingUserAttendanceRecord()) {
            System.out.println(record.toString());
        }
    }

    @RequestMapping(value = "/resetData", method = RequestMethod.GET)
    public void resetData(){
        Long id1 = 31L;
        Long id2 = 35l;
        reset(id1);
        reset(id2);
    }

    private void reset(Long id){
        Optional<DingUserAttendanceRecord> record = dingUserAttendanceRecordRepository.findById(id);
        if (record.isPresent()){
            DingUserAttendanceRecord dingUserAttendanceRecord = record.get();
            dingUserAttendanceRecord.setProjectId(null);
            dingUserAttendanceRecord.setUserCheckTime(new Date());
            dingUserAttendanceRecordRepository.saveAndFlush(dingUserAttendanceRecord);
        }
    }
}
