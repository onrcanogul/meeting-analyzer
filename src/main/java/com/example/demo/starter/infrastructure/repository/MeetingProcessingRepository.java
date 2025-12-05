package com.example.demo.starter.infrastructure.repository;

import com.example.demo.starter.domain.entity.MeetingProcessingJob;
import com.example.demo.starter.domain.enumeration.ProcessingState;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeetingProcessingRepository extends BaseRepository<MeetingProcessingJob> {
    List<MeetingProcessingJob> findByUserId(UUID userId);
    Optional<MeetingProcessingJob> findFirstByStateOrderByIdAsc(ProcessingState state);
}
