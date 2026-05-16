package com.library.controller;

import com.library.model.ApiResponse;
import com.library.model.BookIssue;
import com.library.service.BookIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class BookIssueController {

    private final BookIssueService issueService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookIssue>> issueBook(@RequestBody Map<String, String> request) {
        String bookId   = request.get("bookId");
        String memberId = request.get("memberId");
        if (bookId == null || memberId == null) {
            throw new IllegalArgumentException("bookId and memberId are required");
        }
        BookIssue issue = issueService.issueBook(bookId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book issued successfully", issue));
    }

    @PatchMapping("/{issueId}/return")
    public ResponseEntity<ApiResponse<BookIssue>> returnBook(@PathVariable String issueId) {
        BookIssue returned = issueService.returnBook(issueId);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", returned));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookIssue>>> getAllIssues(
            @RequestParam(required = false) String status) {
        List<BookIssue> issues;
        if ("active".equalsIgnoreCase(status)) {
            issues = issueService.getActiveIssues();
        } else if ("overdue".equalsIgnoreCase(status)) {
            issues = issueService.getOverdueIssues();
        } else {
            issues = issueService.getAllIssues();
        }
        return ResponseEntity.ok(ApiResponse.success("Issues retrieved", issues));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookIssue>> getIssueById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Issue found", issueService.getIssueById(id)));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<BookIssue>>> getIssuesByMember(@PathVariable String memberId) {
        return ResponseEntity.ok(ApiResponse.success("Member issues retrieved",
                issueService.getIssuesByMember(memberId)));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<BookIssue>>> getIssuesByBook(@PathVariable String bookId) {
        return ResponseEntity.ok(ApiResponse.success("Book issues retrieved",
                issueService.getIssuesByBook(bookId)));
    }
}
