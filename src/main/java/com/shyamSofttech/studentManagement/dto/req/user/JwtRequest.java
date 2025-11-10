package com.shyamSofttech.studentManagement.dto.req.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class JwtRequest {
    private String userName;
    private String password;
    private Long mobileNo;
}
