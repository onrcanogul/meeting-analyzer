package com.example.demo.starter.application.dto.meeting;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.domain.enumeration.ProcessingState;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MeetingProcessingJobDto extends BaseDto {
    private UUID meetingId;
    private UUID userId;
    @Enumerated(EnumType.STRING)
    private ProcessingState state;
    private int progress;
    private String error;
}
