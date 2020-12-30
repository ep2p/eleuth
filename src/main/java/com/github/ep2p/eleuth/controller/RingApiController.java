package com.github.ep2p.eleuth.controller;

import com.github.ep2p.eleuth.model.dto.api.BaseResponse;
import com.github.ep2p.eleuth.model.dto.api.RingMemberResponse;
import com.github.ep2p.eleuth.model.entity.file.RingMemberEntity;
import com.github.ep2p.eleuth.service.RingKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//todo: this controller needs Spring Security
//used to generating a ring key, exporting partial keys, or adding keys before bootstrapping ring
@RestController
@RequestMapping("/api")
public class RingApiController {
    private final RingKeyService ringKeyService;

    @Autowired
    public RingApiController(RingKeyService ringKeyService) {
        this.ringKeyService = ringKeyService;
    }

    @GetMapping("/ring/key/generate")
    public @ResponseBody
    BaseResponse generateRingKey(){
        ringKeyService.generate();
        return new BaseResponse(BaseResponse.Status.SUCCESS);
    }

    @PostMapping("/ring/key/export")
    public @ResponseBody
    RingMemberResponse exportRingMembership(@RequestParam(value = "partial", defaultValue = "false") boolean partial, @RequestParam(value = "part", defaultValue = "1") int part){
        RingMemberEntity ringMemberEntity;
        if(partial){
            ringMemberEntity = ringKeyService.exportFullMembership();
        }else {
            ringMemberEntity = ringKeyService.exportPart(part);
        }
        RingMemberResponse ringMemberResponse = new RingMemberResponse();
        ringMemberResponse.setMember(ringMemberEntity);

        return ringMemberResponse;
    }

    @GetMapping("/ring/key/import")
    public @ResponseBody
    BaseResponse importRingMembership(@RequestBody RingMemberEntity ringMemberEntity){
        ringKeyService.importMembership(ringMemberEntity);
        return new BaseResponse(BaseResponse.Status.SUCCESS);
    }

}
