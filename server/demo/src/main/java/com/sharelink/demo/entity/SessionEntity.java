package com.sharelink.demo.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
@Table(name = "sessions")
public class SessionEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "session_id")
    private String sessionId;

    @NotNull
    @Column(name = "object_id")
    private long objectId;
}
