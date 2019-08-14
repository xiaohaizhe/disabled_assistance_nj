package com.hd.home_disabled.socket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.service.DingUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;

public class ThreadForChoosingProject extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(ThreadForChoosingProject.class);
    private Session session;
    private DingUserService dingUserService;

    public ThreadForChoosingProject(Session session) {
        this.session = session;
    }


    @Override
    public void run() {
        try {
            this.dingUserService= BeanContext.getApplicationContext().getBean(DingUserService.class);
            List<DingUserAttendanceRecord> dingUserAttendanceRecords = dingUserService.getDingUserAttendanceRecord();
            logger.info("一共有"+dingUserAttendanceRecords.size()+"条数据");
            JSONArray array = new JSONArray();
            for (DingUserAttendanceRecord record:
                    dingUserAttendanceRecords) {
                if (record.getProjectId()==null){
                    logger.info("根据钉钉用户id："+record.getDingUserId()+"查找系统用户");
                    if (dingUserService.getUserId(record.getDingUserId())!=null){
                        User user = dingUserService.getUserId(record.getDingUserId());
                        JSONObject object = new JSONObject();
                        object.put("id",record.getId());
                        object.put("name",user.getName());
                        object.put("disabilityCertificateNumber",user.getDisabilityCertificateNumber());
                        array.add(object);
                    }
                }
            }
            this.session.getBasicRemote().sendText(array.toJSONString());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}