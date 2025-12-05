package com.example.demo.starter.application.service.meeting.impl;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.service.audio.AudioService;
import com.example.demo.starter.application.service.auth.CustomUserDetailsService;
import com.example.demo.starter.application.service.base.impl.BaseServiceImpl;
import com.example.demo.starter.application.service.meeting.MeetingProcessingJobService;
import com.example.demo.starter.application.service.meeting.MeetingService;
import com.example.demo.starter.application.service.pbi.ProductBacklogItemService;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.domain.entity.Team;
import com.example.demo.starter.domain.enumeration.MeetingStatus;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.infrastructure.configuration.mapper.Mapper;
import com.example.demo.starter.infrastructure.exception.NotFoundException;
import com.example.demo.starter.infrastructure.repository.MeetingRepository;
import com.example.demo.starter.infrastructure.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class MeetingServiceImpl extends BaseServiceImpl<Meeting, MeetingDto> implements MeetingService {
    private final Mapper<Meeting, MeetingDto> mapper;
    private final MeetingRepository repository;
    private final AudioService audioService;
    private final ProductBacklogItemService productBacklogItemService;
    private final CustomUserDetailsService userService;
    private final TeamRepository teamRepository;
    private final MeetingProcessingJobService jobProcessingService;


    public MeetingServiceImpl(MeetingRepository repository,
                              Mapper<Meeting, MeetingDto> mapper,
                              AudioService audioService, ProductBacklogItemService productBacklogItemService, CustomUserDetailsService userService,
                              TeamRepository teamRepository, MeetingProcessingJobService jobProcessingService
    ) {
        super(repository, mapper);
        this.mapper = mapper;
        this.repository = repository;
        this.audioService = audioService;
        this.productBacklogItemService = productBacklogItemService;
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.jobProcessingService = jobProcessingService;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<List<MeetingDto>> get() {
        var meetings = repository.findAllWithRelations();
        var dtoList = meetings.stream().map(a -> {
            var dto = mapper.toDto(a);
            dto.setTranscript("");
            return dto;
        }).toList();
        return ServiceResponse.success(dtoList, 200);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<List<MeetingDto>> getByTeam() {
        UUID teamId = userService.getCurrentTeamId();
        List<Meeting> meetings = repository.findByTeam(teamId);
        List<MeetingDto> dtoList = meetings.stream().map(a -> {
            MeetingDto dto = mapper.toDto(a);
            dto.setTranscript("");
            return dto;
        }).toList();
        return ServiceResponse.success(dtoList, 200);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse<MeetingDto> getById(UUID id) {
        var meeting = repository.findByIdWithRelations(id).orElseThrow(
                () -> new NotFoundException("Meeting Not Found")
        );
        var dto = mapper.toDto(meeting);
        meeting.setTranscript("");
        return ServiceResponse.success(dto, 200);
    }

    @Override
    @Transactional
    public ServiceResponse<MeetingDto> upload(MultipartFile file, String title, String repositoryId, ProviderType providerType) throws IOException, InterruptedException {
        String transcript = audioService.processAudioAndTranscribe(file);
        Meeting meeting = Meeting.builder()
                .title(title)
                .transcript(transcript)
                .status(MeetingStatus.UPLOADED)
                .repositoryId(repositoryId)
                .repositoryProvider(providerType)
                .build();

        Meeting createdMeeting = repository.save(meeting);

        jobProcessingService.create(createdMeeting.getId());

        return ServiceResponse.success(mapper.toDto(createdMeeting), 201);
    }

    @Override
    @Transactional
    public ServiceResponse<MeetingDto> upload(String transcript, String title, String repositoryId, ProviderType providerType) {
        Team team = teamRepository.findById(userService.getCurrentTeamId())
                .orElseThrow(
                        () -> new NotFoundException("Team Not Found")
                );

        Meeting meeting = Meeting.builder()
                .title(title)
                .transcript(transcript)
                .status(MeetingStatus.UPLOADED)
                .team(team)
                .repositoryId(repositoryId)
                .repositoryProvider(providerType)
                .build();

        Meeting createdMeeting = repository.save(meeting);

        jobProcessingService.create(createdMeeting.getId());

        return ServiceResponse.success(mapper.toDto(createdMeeting), 201);
    }

    @Override
    @Transactional
    public ServiceResponse<MeetingDto> updateRepository(UUID meetingId, String repositoryId, ProviderType providerType) {
        Meeting meeting = repository.findById(meetingId).orElseThrow(
                () -> new NotFoundException("Meeting Not Found")
        );

        meeting.setRepositoryId(repositoryId);
        meeting.setRepositoryProvider(providerType);

        Meeting savedMeeting = repository.save(meeting);
        return ServiceResponse.success(mapper.toDto(savedMeeting), 200);
    }

    @Override
    protected void updateEntity(MeetingDto dto, Meeting entity) {
        entity = mapper.toEntity(dto);
    }
}
