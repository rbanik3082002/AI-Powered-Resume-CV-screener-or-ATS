AI Powered Applicant Screening Platform 



Purpose

It's an AI-assisted hiring tool that helps a recruiter go from "here's a job and a pile of resumes" to "here's who to interview and when" — automating the tedious parts (reading every resume, scoring against the JD, answering questions about the pool) while keeping a human recruiter in the loop for the actual decisions.

End-to-end flow it supports

A recruiter logs in and posts a job description.
They upload candidate resumes (PDF/DOCX/TXT).
The AI parses each resume and scores it against that job description, with a rationale (strengths/gaps).
Candidates get ranked; the recruiter sets a threshold to form a shortlist.
The recruiter can ask free-form questions across the whole candidate pool ("who has the strongest cloud experience?").
Shortlisted candidates get interview slots scheduled.



Built an AI powered CV screener of candidates to test and check their eligibility for technical interviews or moving forward to the next rounds of the selection process for the job. Tech Stack used-Java Spring Boot + Spring AI orchestrated Full stack single page ReactJS webpage 


The two halves of the stack

Layer	What it's for
React + TypeScript frontend (the artifact I built first)	The recruiter's actual UI — upload resumes, view scores/rankings, chat with the AI, book interviews. Right now it's a self-contained in-browser demo calling the AI directly, so you can see the concept working immediately.
Java 21 / Spring Boot backend (what we've been building since)	The real system of record: auth, persistence, parsing, and AI orchestration that a production version needs — things a browser-only demo can't safely or durably do.


Frontend or UI/UX- React.js, HTML, Tailwind CSS


Backend- Spring Boot 


Authorrization/JWT (Json web token)


Parsing logic that is the resume Parsing service used - PDFBox + PDF POI


Spring AI integretion and orchestration with the backend and authorization 


Database- Postgre SQL









Feature rundown per screen:

Screen — paste/drop resumes, edit the job description,
Hit "Score candidate" (or "Load samples" to demo instantly). Each candidate shows a live status (scoring → scored) with a score bar.
Shortlist — a threshold slider filters the ranked list; each row shows rank, name, AI summary, strengths/gaps, and the numeric score.
Ask — a chat box to ask free-form questions across the whole candidate pool ("who's strongest in cloud?").
Schedule — for anyone past the threshold, pick a date/time and confirm — turns into a "Scheduled ✓" badge.
