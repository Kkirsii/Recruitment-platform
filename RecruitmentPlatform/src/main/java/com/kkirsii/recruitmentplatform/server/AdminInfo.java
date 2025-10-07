package com.kkirsii.recruitmentplatform.server;


import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AdminInfo {
    void getResume(HttpServletResponse response, String email);

    void getImage(HttpServletResponse response, String email);



    void sendInvitation(String email, Integer jobId, String location, LocalTime time, LocalDate date);

    void send_offer(String email, Integer jobId);
}
