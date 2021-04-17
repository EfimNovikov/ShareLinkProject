package com.sharelink.demo.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor @Builder @NoArgsConstructor
@Table(name = "share_objects")
public class ShareObjectEntity {
    @Id
    private int id;

    @NotNull
    @Column(name = "object_data")
    private String shareText;

    @NotNull
    @Column(columnDefinition = "DATETIME", name = "creation_time")
    private LocalDateTime creationTime;

    @NotNull
    @Column(name = "share_code")
    private int display_code;
}
