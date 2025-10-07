package com.kkirsii.recruitmentplatform.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
@NoArgsConstructor
public class Result {
    private int code;
    private String msg;
    private Object data;
    public static Result success(Object data) {
        log.info("Result success with data: {}", data);
        return new Result(200,"success", data);
    }
    public static Result success() {
        log.info("Result success with no data");
        return new Result(200,"success", null);
    }
    public static Result error() {
        log.info("Result error with no data");
        return new Result(400,"error", null);
    }
    public static Result error(Object data) {
        log.info("Result error with data: {}", data);
        return new Result(400,"error", data);
    }
}
