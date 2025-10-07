package com.kkirsii.recruitmentplatform.pojo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class InvitationInfo {

    private LocalDate date;
    private LocalTime time;
    private String jobType;
    private String location;
    private String contactEmail;
}
