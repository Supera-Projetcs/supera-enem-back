package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.DashboardDTO;
import com.supera.enem.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping("/")
    public DashboardDTO getDashboard() {
        DashboardDTO dashboardDTO = new DashboardDTO();
        long numberOfRightAnswers = dashboardService.getNumberOfRightAnswers();
        long numberOfWrongAnswers = dashboardService.getNumberOfWrongAnswers();
        dashboardDTO.setNumberOfRightAnswers(numberOfRightAnswers);
        dashboardDTO.setNumberOfWrongAnswers(numberOfWrongAnswers);
        dashboardDTO.setNumberOfTests(dashboardService.getNumberOfTests());
        if (numberOfRightAnswers + numberOfWrongAnswers == 0) {
            dashboardDTO.setHitRate(0);
        } else {
            dashboardDTO.setHitRate((double) numberOfRightAnswers / (numberOfRightAnswers + numberOfWrongAnswers));
        }
        return dashboardDTO;
    }

}
