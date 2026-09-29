package com.echoforcode.demobank.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Getter
@Setter
public class UserDto {
    @NotBlank @Size(max = 100)
    private String firstname;
    @NotBlank @Size(max = 100)
    private String lastname;
    @NotBlank @Email @Size(max = 200)
    private String username;
    @NotBlank @Size(min = 8, max = 72)
    private String password;
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date dob;
    private long tel;
    @Size(max = 50)
    private String gender;
}
