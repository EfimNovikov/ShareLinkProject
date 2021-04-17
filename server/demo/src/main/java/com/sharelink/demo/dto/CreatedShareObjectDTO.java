package com.sharelink.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class CreatedShareObjectDTO {
    private String displayCode;
    private String shareObject;
    private LocalDateTime createdTime;
    private long id;
}
