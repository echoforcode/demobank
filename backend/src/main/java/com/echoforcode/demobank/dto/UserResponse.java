package com.echoforcode.demobank.dto;

import com.echoforcode.demobank.entity.User;
import java.util.List;
public record UserResponse(String uid, String firstname,
                           String lastname, String username, String tag,
                           List<String> roles) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getUid(), user.getFirstname(),
                user.getLastname(), user.getUsername(), user.getTag(),
                user.getRoles() == null ? List.of() : user.getRoles());
    }
}