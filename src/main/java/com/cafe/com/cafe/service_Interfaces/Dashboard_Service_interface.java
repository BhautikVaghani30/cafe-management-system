package com.cafe.com.cafe.service_Interfaces;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface Dashboard_Service_interface {
    // returns count of each database api --> to display on dashboard
    ResponseEntity<Map<String, Object>> getCount();
}
