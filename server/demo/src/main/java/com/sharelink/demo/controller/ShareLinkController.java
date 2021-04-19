package com.sharelink.demo.controller;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.entity.ShareObjectEntity;
import com.sharelink.demo.service.ShareObjectService;
import com.sharelink.demo.service.tools.mapper.ShareObjectDTOMapper;
import com.sharelink.demo.service.tools.mapper.StringId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
public class ShareLinkController {

    @Autowired
    ShareObjectService shareObjectService;

    @Autowired
    ShareObjectDTOMapper objectMapper;

    @PostMapping("/api/newShare")
    @ResponseBody
    public CreatedShareObjectDTO createdShareObjectDTO(@RequestBody NewShareObjectDTO newShareObjectDTO,
                                                       HttpServletResponse response){
        SecureRandom secureRandom = new SecureRandom();
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        byte[] buffer = new byte[20];
        secureRandom.nextBytes(buffer);
        response.addCookie(new Cookie("share_uid",encoder.encodeToString(buffer)));
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

    @GetMapping("/api/getShare/{displayCode}")
    public CreatedShareObjectDTO getShareObject (@PathVariable(name = "displayCode") String displayCode){
        int displayCodeInt;
        try {
            displayCodeInt = Integer.parseInt(displayCode);
        } catch (NumberFormatException e){
            return CreatedShareObjectDTO.builder().build();
        }
        Optional<ShareObjectEntity> entity = shareObjectService.getShareObject(displayCodeInt);
        if (entity.isEmpty())
            return CreatedShareObjectDTO.builder().build();
        else {
            CreatedShareObjectDTO createdShareObjectDTO = objectMapper.mapShareEntityToDTO(entity.get());
            createdShareObjectDTO.setDisplayCode(StringId.parseStringId(entity.get().getDisplayCode()));
            createdShareObjectDTO.setCreatedTime(entity.get().getCreationTime());
            return createdShareObjectDTO;
        }
    }

    @DeleteMapping("/api/deleteShare/{id}")
    public ResponseEntity<String> deleteShareObject(@PathVariable(name = "id")long id){
        return shareObjectService.deleteShareObject(id);
    }
}
