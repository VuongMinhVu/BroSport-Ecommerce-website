package com.se2.demo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUpdateRequest {

    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    String fullName;

    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    String phone;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    String address;

    @Size(max = 255, message = "Avatar URL không được vượt quá 255 ký tự")
    String avatarUrl;
}