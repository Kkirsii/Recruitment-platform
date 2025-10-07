package com.kkirsii.recruitmentplatform.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpInfo {
    private String email;
    private String password;
    private String code;
}
