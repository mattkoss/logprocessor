package com.saraphie.logprocessor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private State state;

    private Long timestamp;
    private String type;
    private String host;
    private Long duration;
    private boolean alert;
}
