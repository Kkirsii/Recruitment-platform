package com.kkirsii.recruitmentplatform.ws;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkirsii.recruitmentplatform.pojo.entity.WsMsg;
import com.kkirsii.recruitmentplatform.util.JwtUtil;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/chat")
@Component

public class ChatEndpoint {
    public static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();





    public static String getEmail(Session session) {
        String query = session.getQueryString();
        String token = query.split("=")[1];
        String useremail = JwtUtil.getEmail(token);
        return useremail;
    }
    @OnOpen
    public void onOpen(Session session) {

        String email = getEmail(session);
        System.out.println("WebSocket 连接成功:"+email);
        onlineUsers.put(email, session);
        broadcastMessage(email,"加入聊天室");
        broadcastMember();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到消息：" + message);
        String email = getEmail(session);
        broadcastMessage(email,message);
    }

    @OnClose
    public void onClose(Session session) {

        String email=getEmail(session);
        onlineUsers.remove(email);
        System.out.println("连接关闭");
        broadcastMember();
    }

    // 广播消息给所有在线用户
    private void broadcastMessage(String email,String message) {
        WsMsg wsMsg = new WsMsg("msg",email,message,System.currentTimeMillis());

        try {
            String json= JSON.toJSONString(wsMsg);
            for (Session session : onlineUsers.values()) {
                session.getBasicRemote().sendText(json); // 发送消息
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void broadcastMember() {
        List<String> list = new ArrayList<>();
        for (Session s : onlineUsers.values()) {
            list.add(ChatEndpoint.getEmail(s));
        }

        // 创建一个包含成员和类型的 map 对象
        Map<String, Object> data = new HashMap<>();
        data.put("type", "members");
        data.put("members", list);

        try {
            String json = JSON.toJSONString(data); // FastJSON 会自动转为 {"type":"members", "members":[...]}
            for (Session session : onlineUsers.values()) {
                session.getBasicRemote().sendText(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
