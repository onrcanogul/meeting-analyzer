package com.example.demo.starter.application.service.meeting.impl;

import com.example.demo.starter.application.dto.meeting.MeetingProcessingJobDto;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.meeting.MeetingProcessingJobService;
import com.example.demo.starter.domain.entity.MeetingProcessingJob;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.MeetingProcessingRepository;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.example.demo.starter.domain.enumeration.ProcessingState.QUEUED;

@Service
@RequiredArgsConstructor
public class MeetingProcessingJobServiceImpl implements MeetingProcessingJobService {
    private final MeetingProcessingRepository repository;
    private final CustomUserDetailsService userService;
    private final Mapper<MeetingProcessingJob, MeetingProcessingJobDto> mapper;

    @Override
    public ServiceResponse<List<MeetingProcessingJobDto>> findByUserId() {
        UUID userId = userService.getCurrentUserId();
        var list = repository.findByUserId(userId);
        var dtoList = list.stream().map(mapper::toDto).toList();
        return ServiceResponse.success(dtoList, 200);
    }

    public void create(UUID meetingId) {
        UUID userId = userService.getCurrentUserId();
        MeetingProcessingJob job = new MeetingProcessingJob();
        job.setMeetingId(meetingId);
        job.setState(QUEUED);
        job.setUserId(userId);
        repository.save(job);
    }
}
