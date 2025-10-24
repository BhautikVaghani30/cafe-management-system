package com.cafe.com.cafe.rest_Interfaces;

import java.security.Principal;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cafe.com.cafe.wrapper.User_Wrapper;

// @RequestMapping(path = "/user/user")
public interface User_Interface {

    
    // user signup api
    @PostMapping(path = "/user/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    // user login api
    @PostMapping(path = "/user/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    // get all user from db api
    @GetMapping(path = "/user/get")
    public ResponseEntity<List<User_Wrapper>> getAllUser();

    // update user from admoin said api
    @PostMapping(path = "/user/update")
    public ResponseEntity<String> update(@RequestBody(required = true) Map<String, String> requestMap);

    // check user token api
    @GetMapping(path = "/user/checkToken")
    public ResponseEntity<String> checkToken();

    // change password api
    @PostMapping(path = "/user/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody(required = true) Map<String, String> requestMap);

    // forgot password api
    @PostMapping(path = "/user/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody(required = true) Map<String, String> requestMap);

    // forgot password api
    @PutMapping(path = "/user/updateRole")
    public ResponseEntity<String> updateUserRole(@RequestBody(required = true) Map<String, String> requestMap);

    // forgot password api
    @PostMapping(path = "/user/sendOTP")
    public ResponseEntity<String> preSignup(@RequestBody(required = true) Map<String, String> requestMap);

    // forgot password api
    @DeleteMapping(path = "/user/delete")
    public ResponseEntity<String> deleteUser(@RequestBody(required = true) Map<String, Integer> requestMap);

    // get all user from db api
    @GetMapping(path = "/user/getUser")
    public ResponseEntity<String> getUser(Principal principal);
}
