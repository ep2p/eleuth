package com.github.ep2p.eleuth.controller;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.api.NodeInformationDto;
import com.github.ep2p.eleuth.service.GeneralApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//todo: this controller needs Spring Security
//Gives out some general information about the node. Useful for exporting or importing nodes information.
@RestController
@RequestMapping("/api")
public class GeneralApiController {
    private final GeneralApiService generalApiService;

    @Autowired
    public GeneralApiController(GeneralApiService generalApiService) {
        this.generalApiService = generalApiService;
    }

    @GetMapping("/node/information")
    public @ResponseBody
    NodeInformationDto getNodeInformation(){
        return generalApiService.getNodeInformationDto();
    }

    @PostMapping("/node/information")
    public @ResponseBody
    BaseResponse addNodeInformation(@RequestBody NodeInformationDto nodeInformationDto){
        return generalApiService.addNodeInformation(nodeInformationDto);
    }

}
