package com.cafe.com.cafe.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class User_Wrapper {

    private Integer id;

    private String name;

    private String email;

    private String contactNumber;

    private String status;
    private String role;

    public User_Wrapper(Integer id, String name, String email, String contactNumber, String status, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.status = status;
        this.role = role;
    }

}
