package com.cafe.com.cafe.rest_controllers;

import com.cafe.com.cafe.rest_Interfaces.Dashboard_interface;
import com.cafe.com.cafe.service_Interfaces.Dashboard_Service_interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class Dashboard_controller implements Dashboard_interface {

    @Autowired
    Dashboard_Service_interface dashboardService;

    // this method is used to get dashaboard details
    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        try {
            return dashboardService.getCount();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
