package com.sharelink.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreatedShareObjectDTO {
    private String id;
    private String shareObject;
    private LocalDateTime createdTime;
}
