package com.shyamSofttech.studentManagement.dto.res.user;

import com.shyamSofttech.studentManagement.constant.Status;
import lombok.*;
import org.springframework.http.client.ClientHttpResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponse {
    String token;
    Long id;
    String userName;
    String name;
    Status status;
    String gender;
    String email;
    ClientHttpResponse clientResponse;
}
