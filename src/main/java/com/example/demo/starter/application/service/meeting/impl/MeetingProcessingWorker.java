package com.example.demo.starter.application.service.meeting.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.domain.entity.MeetingProcessingJob;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import com.example.demo.starter.domain.enumeration.ProcessingState;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.repository.MeetingProcessingRepository;
import com.example.demo.starter.infrastructure.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingProcessingWorker {
    private final MeetingProcessingRepository jobRepository;
    private final MeetingRepository meetingRepository;
    private final Mapper<Meeting, MeetingDto> meetingMapper;
    private final ProductBacklogItemService productBacklogItemService;

    @Scheduled(fixedDelay = 2000)
    public void run() {

        Optional<MeetingProcessingJob> optionalJob = jobRepository.findFirstByStateOrderByIdAsc(ProcessingState.QUEUED);

        if (optionalJob.isEmpty()) return;

        MeetingProcessingJob job = optionalJob.get();

        try {
            job.setState(ProcessingState.PBI_GENERATION);
            job.setProgress(25);
            jobRepository.save(job);

            Meeting meeting = meetingRepository.findById(job.getMeetingId()).orElseThrow(
                    () -> new RuntimeException("Meeting not found")
            );

            MeetingDto meetingDto = meetingMapper.toDto(meeting);

            productBacklogItemService.analyzeAndCreate(meetingDto);

            meeting.setStatus(MeetingStatus.UPLOADED);
            meetingRepository.save(meeting);

            job.setProgress(100);
            job.setState(ProcessingState.COMPLETED);
            jobRepository.save(job);

        } catch (Exception ex) {
            job.setState(ProcessingState.FAILED);
            job.setError(ex.getMessage());
            jobRepository.save(job);
        }
    }

}
