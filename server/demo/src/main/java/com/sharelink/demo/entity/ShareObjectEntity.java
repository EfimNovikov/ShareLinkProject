package com.sharelink.demo.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor @Builder @NoArgsConstructor
@Table(name = "share_objects")
public class ShareObjectEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @NotNull
    @Column(name = "object_data")
    private String shareText;

    @NotNull
    @Column(columnDefinition = "DATETIME", name = "creation_time")
    private LocalDateTime creationTime;

    @NotNull
    @Column(name = "share_code")
    private int displayCode;
}
