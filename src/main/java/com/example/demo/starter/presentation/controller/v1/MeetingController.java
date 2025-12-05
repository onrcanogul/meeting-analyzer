package com.example.demo.starter.presentation.controller.v1;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.meeting.MeetingProcessingJobDto;
import com.example.demo.starter.application.service.meeting.MeetingProcessingJobService;
import com.example.demo.starter.application.service.meeting.MeetingService;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.presentation.controller.base.BaseController;
import com.example.demo.starter.presentation.model.CreateWithTranscriptRequestModel;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meeting")
@RequiredArgsConstructor
public class MeetingController extends BaseController {
    private final MeetingService meetingService;
    private final MeetingProcessingJobService meetingProcessingJobService;


    @GetMapping
    @Operation(summary = "-TEST- Get All Meeting Services")
    public ResponseEntity<ServiceResponse<List<MeetingDto>>> get() {
        return controllerResponse(meetingService.get());
    }

    @GetMapping("team")
    @Operation(summary = "Get Meeting By Team Id")
    public ResponseEntity<ServiceResponse<List<MeetingDto>>> getByTeam() {
        return controllerResponse(meetingService.getByTeam());
    }

    @GetMapping("/{id}")
    @Operation(summary = "-TEST- Get Meeting By Id")
    public ResponseEntity<ServiceResponse<MeetingDto>> get(@PathVariable UUID id) {
        return controllerResponse(meetingService.getById(id));
    }

    @GetMapping("/job/user")
    @Operation(summary = "Get Meeting User")
    public ResponseEntity<ServiceResponse<List<MeetingProcessingJobDto>>> getJobsByUser() {
        return controllerResponse(meetingProcessingJobService.findByUserId());
    }

    @PostMapping
    @Operation(summary = "Create Meeting From Meeting")
    public ResponseEntity<ServiceResponse<MeetingDto>> create(@RequestPart MultipartFile file,
                                                              @RequestPart String title,
                                                              @RequestPart String repositoryId,
                                                              @RequestPart ProviderType providerType
                                                              ) throws IOException, InterruptedException {
        return controllerResponse(meetingService.upload(file, title, repositoryId, providerType));
    }

    @PostMapping("transcript")
    @Operation(summary = "Create Meeting From Transcript")
    public ResponseEntity<ServiceResponse<MeetingDto>> create(@RequestBody CreateWithTranscriptRequestModel model) throws IOException, InterruptedException {
        return controllerResponse(meetingService.upload(model.transcript(), model.title(), model.repositoryId(), model.providerType()));
    }

    @PutMapping("repository")
    @Operation(summary = "Create Meeting From Transcript")
    public ResponseEntity<ServiceResponse<MeetingDto>> updateRepository(@RequestParam UUID meetingId,
                                                                        @RequestParam String repositoryId,
                                                                        @RequestParam ProviderType providerType) {
        return controllerResponse(meetingService.updateRepository(meetingId, repositoryId, providerType));
    }

    @PutMapping
    @Operation(summary = "-TEST- Update Meeting")
    public ResponseEntity<ServiceResponse<MeetingDto>> update(MeetingDto model) {
        return controllerResponse(meetingService.update(model, model.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "-TEST- Delete Meeting")
    public ResponseEntity<ServiceResponse<NoContent>> delete(@PathVariable UUID id) {
        return controllerResponse(meetingService.delete(id));
    }
}
