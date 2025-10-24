package com.cafe.com.cafe.service_Interfaces;

import org.springframework.http.ResponseEntity;
import com.cafe.com.cafe.wrapper.*;

import java.security.Principal;
import java.util.*;

public interface User_Service_Interface {

    // --------------------------------------------------------------------------------------------------------------
    // user sign up api created in
    ResponseEntity<String> signUp(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // user login api created in
    ResponseEntity<String> login(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // user login api created in
    ResponseEntity<List<User_Wrapper>> getAllUser();

    // --------------------------------------------------------------------------------------------------------------
    // user login api created in
    ResponseEntity<String> update(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // user tokencheck
    ResponseEntity<String> checkToken();

    // --------------------------------------------------------------------------------------------------------------
    // change password api
    ResponseEntity<String> changePassword(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // forgot password api
    ResponseEntity<String> forgotPassword(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // update user role
    ResponseEntity<String> updateUserRole(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // update user role
    ResponseEntity<String> preSignup(Map<String, String> requestMap);

    // --------------------------------------------------------------------------------------------------------------
    // update user role
    ResponseEntity<String> deleteUser(Map<String, Integer> requestMap);
    
    ResponseEntity<String> getUser(Principal principal);

}
