package com.sharelink.demo.entity.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReCaptchaResponseType {
    private boolean success;
    private String challenge_ts;
    private String hostname;
}
