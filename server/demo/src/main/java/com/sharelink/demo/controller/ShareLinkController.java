package com.sharelink.demo.controller;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.entity.ShareObjectEntity;
import com.sharelink.demo.service.ReCaptchaValidationService;
import com.sharelink.demo.service.ShareObjectService;
import com.sharelink.demo.service.tools.mapper.ShareObjectDTOMapper;
import com.sharelink.demo.service.tools.StringId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
public class ShareLinkController {

    @Autowired
    ShareObjectService shareObjectService;

    @Autowired
    ShareObjectDTOMapper objectMapper;

    @Autowired
    ReCaptchaValidationService captchaValidationService;

    @PostMapping("/api/newShare")
    @ResponseBody
    public CreatedShareObjectDTO createdShareObjectDTO(@RequestBody NewShareObjectDTO newShareObjectDTO,
                                                       HttpServletRequest request,
                                                       HttpSession httpSession,
                                                       HttpServletResponse response){

        String r = request.getParameter("g-recaptcha-response");
        if (captchaValidationService.recaptchaIsValid(r)) {
            request.getSession();
            CreatedShareObjectDTO createdShareObjectDTO = shareObjectService.createNewShareObject(newShareObjectDTO, httpSession);
            httpSession.setMaxInactiveInterval(5 * 60 * 60);
            return createdShareObjectDTO;
        } else {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return CreatedShareObjectDTO.builder().build();
        }
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

    @PatchMapping("/api/changeShare/{id}")
    public CreatedShareObjectDTO modifyShareObject (@PathVariable(name = "id") long id,
                                                    @RequestBody NewShareObjectDTO newShareObjectDTO,
                                                    HttpSession httpSession,
                                                    HttpServletResponse response){

        CreatedShareObjectDTO createdShareObjectDTO = shareObjectService.modifyShareObject(
                newShareObjectDTO, httpSession.getId(), id
        );
        if (createdShareObjectDTO != null){
            return createdShareObjectDTO;
        }
        else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }
    }

    @DeleteMapping("/api/deleteShare/{id}")
    public ResponseEntity<String> deleteShareObject(@PathVariable(name = "id")long id,
                                                    HttpSession httpSession,
                                                    HttpServletRequest request){
        request.getSession(false);
        return shareObjectService.deleteShareObject(id, httpSession);
    }
}
