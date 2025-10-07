package com.kkirsii.recruitmentplatform.ws;


import com.alibaba.fastjson.JSON;
import jakarta.websocket.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController

public class ChatEndpointinfo {

    @GetMapping("/chat/member")
    public String getMember()
    {
        Map<String, Session>temp=ChatEndpoint.onlineUsers;
        List<String> list=new ArrayList<String>();
        for(Session s:temp.values())
        {
            list.add(ChatEndpoint.getEmail(s));
        }
        String res= JSON.toJSONString(list);
        return res;
    }
}
