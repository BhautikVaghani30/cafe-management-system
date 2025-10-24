package com.cafe.com.cafe.services;

import com.cafe.com.cafe.Entites.User;
import com.cafe.com.cafe.JWT.CustomerUsersDetailsService;
import com.cafe.com.cafe.JWT.JwtFilter;
import com.cafe.com.cafe.JWT.JwtUtil;
import com.cafe.com.cafe.constants.Cafe_Constants;
import com.cafe.com.cafe.repositories.User_Dao;
import com.cafe.com.cafe.service_Interfaces.User_Service_Interface;
import com.cafe.com.cafe.utils.CafeUtils;
import com.cafe.com.cafe.utils.EmailUtils;
import com.cafe.com.cafe.wrapper.User_Wrapper;
import com.google.common.base.Strings;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Slf4j
@Service
public class User_Service implements User_Service_Interface {
    @Autowired
    User_Dao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    EmailUtils emailUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup: {}", requestMap);
        try {
           
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity(Cafe_Constants.SUCCESSFULLY_REGISTERED, HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(Cafe_Constants.DUPLICATE_ACCOUNT, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) throws MessagingException {
        User user = new User();
        List<User> all = userDao.findAll();
        if(all.size() == 0){
            user.setRole("admin");
            user.setStatus("true");
        }else{
            user.setRole("user");
            user.setStatus("false");
        }
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(bCryptPasswordEncoder.encode(requestMap.get("password")));
        user.setTempPassword(requestMap.get("password"));
        user.setOtp(requestMap.get("otp"));
        return user;
    }

    @Override
    public ResponseEntity<String> preSignup(Map<String, String> requestMap) {
        try {
            log.info("Inside presignup: {}", requestMap);
            if (validate(requestMap)) {
                int otp = CafeUtils.generateOTP();
                emailUtil.emailOTP(requestMap.get("email"),"One Time Password" ,otp);
                return CafeUtils.getResponseEntity(String.valueOf(otp), HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      
        return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
    }


    private boolean validate(Map<String, String> requestMap) {
     return requestMap.containsKey("email");
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));

            if (auth.isAuthenticated()) {
                
                if (customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
                                    customerUsersDetailsService.getUserDetail().getRole())
                            + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Please wait for admin approval." + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<User_Wrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                List<User_Wrapper> users = userDao.getAllUser();

                return new ResponseEntity<>(users, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            
            
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optional.isEmpty()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User Status Updated Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User id Doesn't not exist", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String userName, List<String> allAdmin) throws MessagingException {
        allAdmin.remove(jwtFilter.getCurrentUser());
        String currentUser = jwtFilter.getCurrentUser();

        String subject;
        String htmlMessage;

        if (status != null && status.equalsIgnoreCase("true")) {
            subject = "Account Approved";
            htmlMessage = "<div style=\"border: 1px solid #000; padding: 10px;\">"
                    + "<h2 style=\"border-bottom: 1px solid #000;\">Account Approved</h2>"
                    + "<p style=\"margin-bottom: 10px;\">Dear Admin,</p>"
                    + "<p style=\"margin-bottom: 10px;\">The account with the name <strong>" + userName
                    + "</strong> on Namaste Cafe has been approved by <strong>" + jwtFilter.getCurrentUser()
                    + "</strong>.</p>"
                    + "<p style=\"margin-bottom: 10px;\">Once the account is approved, the user can log in using their email address and password to access all the features and benefits of Namaste Cafe.</p>"
                    + "<p style=\"margin-bottom: 10px;\">If you have any questions or need further assistance, please don't hesitate to contact us.</p>"
                    + "<br>"
                    + "<p>Best regards,</p>"
                    + "<p>" + jwtFilter.getCurrentUser() + "</p>"
                    + "</div>";
        } else {
            subject = "Account Disabled";
            htmlMessage = "<div style=\"border: 1px solid #000; padding: 10px;\">"
                    + "<h2 style=\"border-bottom: 1px solid #000;\">Account Disabled</h2>"
                    + "<p style=\"margin-bottom: 10px;\">Dear Admin,</p>"
                    + "<p style=\"margin-bottom: 10px;\">The account with the name <strong>" + userName
                    + "</strong> on Namaste Cafe has been Disabled by <strong>" + jwtFilter.getCurrentUser()
                    + "</strong>.</p>"
                    + "<p style=\"margin-bottom: 10px;\">Once the account is Disabled, the user can't log in using their email address and password to access all the features and benefits of Namaste Cafe.</p>"
                    + "<p style=\"margin-bottom: 10px;\">If you have any questions or need further assistance, please don't hesitate to contact us.</p>"
                    + "<br>"
                    + "<p>Best regards,</p>"
                    + "<p>" + jwtFilter.getCurrentUser() + "</p>"
                    + "</div>";
        }

        emailUtil.sendSimpleMessage(jwtFilter.getCurrentUser(), subject, htmlMessage, allAdmin);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity(Cafe_Constants.TRUE, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObject = userDao.findByEmail(jwtFilter.getCurrentUser());
            if (!userObject.equals(null)) {
                if (bCryptPasswordEncoder.matches(requestMap.get("oldPassword"), userObject.getPassword())) {
                    String newPassword = requestMap.get("newPassword");
                    String hashedPassword = bCryptPasswordEncoder.encode(newPassword);
                    userObject.setPassword(hashedPassword);
                    userDao.save(userObject); // save the data to the db
                    return CafeUtils.getResponseEntity(Cafe_Constants.PASSWORD_CHANGED, HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(Cafe_Constants.INCORRECT_OLD_PASSWORD, HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User userObject = userDao.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(userObject) && !Strings.isNullOrEmpty(userObject.getEmail())) {
                emailUtil.forgotMail(userObject.getEmail(), "Reset your Lofi Cafe Password", userObject.getTempPassword());
                return CafeUtils.getResponseEntity(Cafe_Constants.EMAIL_SENDE, HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity(Cafe_Constants.CHECK_EMAIL, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> updateUserRole(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateUser(requestMap)) {
                    User user = userDao.findById(Integer.parseInt(requestMap.get("id"))).get();
                    if (user != null) {
                        user.setRole(requestMap.get("role"));
                        userDao.save(user);
                        return CafeUtils.getResponseEntity(Cafe_Constants.USER_ROLE_UPDATE, HttpStatus.OK);
                    }
                    return CafeUtils.getResponseEntity(Cafe_Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
                }
                return CafeUtils.getResponseEntity(Cafe_Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            } else {
                return CafeUtils.getResponseEntity(Cafe_Constants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private boolean validateUser(Map<String, String> requestMap) {
        return requestMap.containsKey("id") && requestMap.containsKey("role");
    }
    
    @Override
    public ResponseEntity<String> deleteUser(Map<String, Integer> requestMap) {
        try {
            User user = userDao.findById(requestMap.get("id")).get();
            if(user != null){
                userDao.delete(user);
                return CafeUtils.getResponseEntity(Cafe_Constants.USER_DELETED_SUCCESSFULLY, HttpStatus.OK);

            }else{
                return CafeUtils.getResponseEntity(Cafe_Constants.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @Override
    public ResponseEntity<String> getUser(Principal principal) {
        try {
            User user = userDao.findByEmail(principal.getName());
            return CafeUtils.getResponseEntity(user.getName(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(Cafe_Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    

    
}
