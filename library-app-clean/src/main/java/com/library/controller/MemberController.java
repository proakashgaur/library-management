package com.library.controller;

import com.library.model.ApiResponse;
import com.library.model.Member;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<Member>> registerMember(@Valid @RequestBody Member member) {
        Member saved = memberService.registerMember(member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member registered successfully", saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAllMembers(
            @RequestParam(required = false) String search) {
        List<Member> members = (search != null && !search.isBlank())
                ? memberService.searchMembers(search)
                : memberService.getAllMembers();
        return ResponseEntity.ok(ApiResponse.success("Members retrieved", members));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getMemberById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Member found", memberService.getMemberById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> updateMember(
            @PathVariable String id, @Valid @RequestBody Member member) {
        return ResponseEntity.ok(ApiResponse.success("Member updated", memberService.updateMember(id, member)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Member>> updateStatus(
            @PathVariable String id, @RequestBody Map<String, String> body) {
        Member.MemberStatus status;
        try {
            status = Member.MemberStatus.valueOf(body.get("status").toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status. Valid: ACTIVE, SUSPENDED, EXPIRED");
        }
        return ResponseEntity.ok(ApiResponse.success("Status updated", memberService.updateMemberStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable String id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member deleted", null));
    }
}
