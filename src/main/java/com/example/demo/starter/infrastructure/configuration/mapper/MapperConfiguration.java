package com.example.demo.starter.infrastructure.configuration.mapper;

import com.example.demo.starter.application.dto.base.BaseDto;
import com.example.demo.starter.application.dto.integration.IntegrationTokenDto;
import com.example.demo.starter.application.dto.meeting.MeetingDto;
import com.example.demo.starter.application.dto.meeting.MeetingProcessingJobDto;
import com.example.demo.starter.application.dto.pbi.ProductBacklogItemDto;
import com.example.demo.starter.application.dto.team.TeamDto;
import com.example.demo.starter.application.dto.user.UserDto;
import com.example.demo.starter.domain.entity.*;
import com.example.demo.starter.domain.entity.base.BaseEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {
    @Bean
    public Mapper<BaseEntity, BaseDto> baseMapper() {
        return new Mapper<>(BaseEntity.class, BaseDto.class);
    }
    @Bean
    public Mapper<Meeting, MeetingDto> meetingMapper() {
        return new Mapper<>(Meeting.class, MeetingDto.class);
    }
    @Bean
    public Mapper<User, UserDto> userMapper() {
        return new Mapper<>(User.class, UserDto.class);
    }
    @Bean
    public Mapper<ProductBacklogItem, ProductBacklogItemDto> pbiMapper() { return new Mapper<>(ProductBacklogItem.class, ProductBacklogItemDto.class); }
    @Bean
    public Mapper<Team, TeamDto> teamMapper() { return new Mapper<>(Team.class, TeamDto.class); }
    @Bean
    public Mapper<IntegrationToken, IntegrationTokenDto> tokenMapper() { return new Mapper<>(IntegrationToken.class, IntegrationTokenDto.class); }
    @Bean
    public Mapper<MeetingProcessingJob, MeetingProcessingJobDto> jobMapper() { return new Mapper<>(MeetingProcessingJob.class, MeetingProcessingJobDto.class); }
}