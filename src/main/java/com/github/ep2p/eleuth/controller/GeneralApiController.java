package com.github.ep2p.eleuth.controller;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.api.NodeInformationDto;
import com.github.ep2p.eleuth.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//todo: this controller needs Spring Security
@RestController
@RequestMapping("/api")
public class GeneralApiController {
    private final ApiService apiService;

    @Autowired
    public GeneralApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/node/information")
    public @ResponseBody
    NodeInformationDto getNodeInformation(){
        return apiService.getNodeInformationDto();
    }

    @PostMapping("/node/information")
    public @ResponseBody
    BaseResponse addNodeInformation(@RequestBody NodeInformationDto nodeInformationDto){
        return apiService.addNodeInformation(nodeInformationDto);
    }

}
