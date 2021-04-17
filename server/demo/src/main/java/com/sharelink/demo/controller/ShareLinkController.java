package com.sharelink.demo.controller;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.service.ShareObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShareLinkController {

    @Autowired
    ShareObjectService shareObjectService;

    @PostMapping("/api/newShare")
    @ResponseBody
    public CreatedShareObjectDTO createdShareObjectDTO(@RequestBody NewShareObjectDTO newShareObjectDTO){
        return shareObjectService.createNewShareObject(newShareObjectDTO);
    }

    @GetMapping(value = "/api/getShares")
    @ResponseBody
    public List<CreatedShareObjectDTO> getShares(@RequestParam(name = "page", defaultValue = "0", required = false)String page){
        int pageInt;
        try {
            pageInt = Integer.parseInt(page);
        } catch (Exception e){
            pageInt = 0;
        }
        return shareObjectService.getShareObjects(PageRequest.of(pageInt, 25));
    }
}
