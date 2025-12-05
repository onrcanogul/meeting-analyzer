package com.example.demo.starter.domain.entity;

import com.example.demo.starter.domain.entity.base.BaseEntity;
import com.example.demo.starter.domain.enumeration.ProcessingState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MeetingProcessingJob extends BaseEntity {
    private UUID meetingId;
    private UUID userId;
    @Enumerated(EnumType.STRING)
    private ProcessingState state;
    private int progress;
    private String error;
}
