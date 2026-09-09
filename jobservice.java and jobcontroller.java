// Job Service code
//Purpose ----->
JobService.java — the business/data layer

This is where the actual logic lives:

create() — builds a Job entity from the incoming request plus the creator, and persists it via JobRepository
findAll() — lists every job posting
findByIdOrThrow() — fetches one job, or throws a clean 404 Not Found (ResponseStatusException) if it doesn't exist, instead of leaking a raw NoSuchElementException up to the client

Why split them at all?

Separation of concerns: controllers stay thin (HTTP/JSON concerns only), services own the logic and transaction boundaries. This is the same pattern you'll see repeated for CandidateController/CandidateService and would see for InterviewController/InterviewService.
Reusability: JobService can be called from other services too (e.g. CandidateService will call JobService.findByIdOrThrow() when a candidate is uploaded, to confirm the job exists and grab its description for scoring) — without needing to go through HTTP.
Testability: you can unit-test JobService with a mocked JobRepository and no web server involved at all; controller tests stay focused on request/response shape and status codes.

In short: Job is the entity/table, JobRepository talks to the database, JobService is the logic that sits above the repository, and JobController is the thin REST wrapper recruiters' frontend actually calls to create and list job postings.


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

.
.
.
.
.
.
.
.
.
.
.
//job controller code

// Purpose----> 
JobController.java — the HTTP layer

It's the entry point for anything related to job postings over REST. It doesn't contain any business logic itself — its only job is to:

Receive HTTP requests (POST /api/jobs, GET /api/jobs, GET /api/jobs/{jobId})
Validate/deserialize the request body (@Valid @RequestBody JobRequest)
Pull out the logged-in user via @AuthenticationPrincipal User currentUser (populated by your JWT filter)
Delegate the actual work to JobService
Convert the returned Job entity into a JobResponse DTO before sending it back, so you're never leaking your JPA entity (and its lazy-loaded fields) straight into the API response


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
