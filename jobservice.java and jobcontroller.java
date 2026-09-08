package com.sift.screener.job;

import com.sift.screener.job.dto.JobRequest;
import com.sift.screener.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job create(JobRequest request, User creator) {
        Job job = Job.builder()
                .title(request.title())
                .description(request.description())
                .createdBy(creator)
                .build();
        return jobRepository.save(job);
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Job findByIdOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job " + id + " not found"));
    }
}















package com.sift.screener.job;

import com.sift.screener.job.dto.JobRequest;
import com.sift.screener.job.dto.JobResponse;
import com.sift.screener.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest request,
                                               @AuthenticationPrincipal User currentUser) {
        Job job = jobService.create(request, currentUser);
        return ResponseEntity.ok(JobResponse.from(job));
    }

    @GetMapping
    public List<JobResponse> listAll() {
        return jobService.findAll().stream().map(JobResponse::from).toList();
    }

    @GetMapping("/{jobId}")
    public JobResponse getOne(@PathVariable Long jobId) {
        return JobResponse.from(jobService.findByIdOrThrow(jobId));
    }
}
