package com.hd.home_disabled.socket;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import com.hd.home_disabled.service.AdminService;
import com.hd.home_disabled.service.DingUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket")
@Component
public class CheckInWebsocket {
    private static final Logger logger = LoggerFactory.getLogger(CheckInWebsocket.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<CheckInWebsocket> webSocketSet = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    ThreadForChoosingProject thread;
    private DingUserService dingUserService;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.info("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage("成功建立连接");
            thread = new ThreadForChoosingProject(session);
            thread.start();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        logger.info("来自客户端的消息:" + message);
        /*if (message.equals("on")){
            thread = new ThreadForChoosingProject(session);
            thread.start();
        }else{
            dealWithMessage(message);
        }*/
    }

//    private void dealWithMessage(String message) {
//        JSONObject object = JSONObject.parseObject(message);
//        logger.info(object.toJSONString());
//        DingUserAttendanceRecord record = new DingUserAttendanceRecord();
//        if (object.getLong("id")!=null
//                && object.getInteger("projectId") !=null) {
//            this.dingUserService = BeanContext.getApplicationContext().getBean(DingUserService.class);
//            Long id = object.getLong("id");
//            Integer projectId = object.getInteger("projectId");
//            if (dingUserService.getById(id) != null) {
//                DingUserAttendanceRecord dingUserAttendanceRecord = dingUserService.getById(id);
//                dingUserAttendanceRecord.setProjectId(projectId);
//                dingUserService.updateDingUserAttendanceRecord(dingUserAttendanceRecord);
//            }
//        }
//    }

    private void sendMessage(String message) throws IOException {
      this.session.getBasicRemote().sendText(message);
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        CheckInWebsocket.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        CheckInWebsocket.onlineCount--;
    }
}
