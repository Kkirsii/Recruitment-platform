package com.kkirsii.recruitmentplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsMsg {
    private String type;
    private String email;
    private String msg;
    private long time;
}
