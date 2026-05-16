package com.library.controller;

import com.library.model.ApiResponse;
import com.library.service.BookIssueService;
import com.library.service.BookService;
import com.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final BookService bookService;
    private final MemberService memberService;
    private final BookIssueService issueService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = Map.of(
                "totalBooks",          bookService.getTotalBooks(),
                "availableBooks",      bookService.getAvailableBooks().size(),
                "totalMembers",        memberService.getTotalMembers(),
                "totalIssues",         issueService.getTotalIssues(),
                "activeIssues",        issueService.getActiveIssues().size(),
                "overdueIssues",       issueService.getOverdueIssues().size()
        );
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
    }
}
