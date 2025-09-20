package com.teleeza.wallet.teleeza.location.controller;

import com.teleeza.wallet.teleeza.location.model.Location;
import com.teleeza.wallet.teleeza.location.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/teleeza/")
@Slf4j
public class LocationController {
    @Autowired
    private LocationRepository locationRepository;

    @GetMapping("locations")
    public Map<String, List<Location>> getLocations(){
        Map<String, List<Location>> response = new HashMap<>();
        response.put("counties", locationRepository.findAll());
        return response;
    }

    @GetMapping("counties")
    public Map<String, List<String>> getCounties(){
        Map<String, List<String>> response = new HashMap<>();
        response.put("counties", locationRepository.getCountyNames());
        return response;
    }
}
