package com.sharelink.demo.service;

import com.sharelink.demo.config.CaptchaSettings;
import com.sharelink.demo.entity.types.ReCaptchaResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

@Service
public class ReCaptchaValidationService {

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Autowired
    CaptchaSettings captchaSettings;

    private boolean isSane(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    public boolean recaptchaIsValid(String captchaResp){
        if (isSane(captchaResp)) {
            MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
            requestMap.add("secret", captchaSettings.getSecret());
            requestMap.add("response", captchaResp);

            RestTemplate restTemplate = new RestTemplate();
            ReCaptchaResponseType response = restTemplate.postForObject(
                    CaptchaSettings.GOOGLE_RECAPTCHA_ENDPOINT,
                    requestMap,
                    ReCaptchaResponseType.class
            );
            if (response == null)
                return false;
            else
                return Boolean.TRUE.equals(response.isSuccess());
        } else {
            return false;
        }
    }
}
