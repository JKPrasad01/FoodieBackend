package com.example.FoodApp.controller;


import com.example.FoodApp.service.Service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashBoardController {
    private final DashBoardService dashBoardService;


    @GetMapping("/admin")
    public Map<String,Long> fetchAdminDashBoardDetails(){
        return dashBoardService.fetchAllDetails();
    }
}
