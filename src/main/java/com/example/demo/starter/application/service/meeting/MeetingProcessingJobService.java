package com.example.demo.starter.application.service.meeting;

import com.example.demo.starter.application.dto.meeting.MeetingProcessingJobDto;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;

import java.util.List;
import java.util.UUID;

public interface MeetingProcessingJobService {
    ServiceResponse<List<MeetingProcessingJobDto>> findByUserId();
    void create(UUID meetingId);
}
