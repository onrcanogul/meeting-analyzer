package com.example.demo.starter.application.service.ai;

import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.domain.entity.Meeting;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;

import java.util.List;

public interface AIService {
    ServiceResponse<List<ProductBacklogItemDto>> analyzeBacklog(Meeting meeting);
}
