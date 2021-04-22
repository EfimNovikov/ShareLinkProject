package com.sharelink.demo.controller;

import com.sharelink.demo.dto.CreatedShareObjectDTO;
import com.sharelink.demo.dto.NewShareObjectDTO;
import com.sharelink.demo.entity.ShareObjectEntity;
import com.sharelink.demo.service.ReCaptchaValidationService;
import com.sharelink.demo.service.ShareObjectService;
import com.sharelink.demo.service.tools.mapper.ShareObjectDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
public class ShareLinkController {

    private final static Pattern DISPLAY_CODE_PATTERN = Pattern.compile("^[0-9]+$");

    private boolean isSane(String code){
        return StringUtils.hasLength(code) && DISPLAY_CODE_PATTERN.matcher(code).matches();
    }

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
        //TODO: remove or clause before production
        if (captchaValidationService.recaptchaIsValid(r) || true) {
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
        if (isSane(displayCode)) {
            Optional<ShareObjectEntity> entity = shareObjectService.getShareObject(displayCode);
            if (entity.isEmpty())
                return CreatedShareObjectDTO.builder().build();
            else {
                CreatedShareObjectDTO createdShareObjectDTO = objectMapper.mapShareEntityToDTO(entity.get());
                createdShareObjectDTO.setDisplayCode(entity.get().getDisplayCode());
                createdShareObjectDTO.setCreatedTime(entity.get().getCreationTime());
                return createdShareObjectDTO;
            }
        } else {
            return CreatedShareObjectDTO.builder().build();
        }
    }

    @GetMapping("/api/search")
    public List<CreatedShareObjectDTO> searchShareObject(@RequestParam(name = "term") String term){
        if (isSane(term))
            return shareObjectService.searchLike(term);
        else
            return null;
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
