package com.cafe.com.cafe.utils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CafeUtils {

    private CafeUtils() {

    }

    // displays api response message
    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<String>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
    }

    public static String getUUID() {
        Date date = new Date();
        Long time = date.getTime();
        return "BILL-" + time;
    }

    // converts the string in the request body into a json array
    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data) {
        if (!Strings.isNullOrEmpty(data)) {
            // json string to java object
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {

            }.getType());
        }
        return new HashMap<>();
    }

    // checks if the given file path is validpa
    public static Boolean isFileExist(String path) {
        log.info("Inside isFileExist {}", path);
        try {
            File file = new File(path);
            return (file != null && file.exists() ? Boolean.TRUE : Boolean.FALSE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static int generateOTP() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return otp;
    }
}
