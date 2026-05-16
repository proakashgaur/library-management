package com.library.service;

import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Member;
import com.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member registerMember(Member member) {
        log.info("Registering member with email: {}", member.getEmail());
        memberRepository.findByEmail(member.getEmail()).ifPresent(existing -> {
            throw new DuplicateResourceException("Member with email " + member.getEmail() + " already exists");
        });
        return memberRepository.save(member);
    }

    public Member getMemberById(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public List<Member> searchMembers(String keyword) {
        return memberRepository.search(keyword);
    }

    public Member updateMember(String id, Member updatedMember) {
        Member existing = getMemberById(id);

        if (!existing.getEmail().equals(updatedMember.getEmail())) {
            memberRepository.findByEmail(updatedMember.getEmail()).ifPresent(m -> {
                throw new DuplicateResourceException("Email already in use: " + updatedMember.getEmail());
            });
        }

        existing.setName(updatedMember.getName());
        existing.setEmail(updatedMember.getEmail());
        existing.setPhone(updatedMember.getPhone());
        existing.setAddress(updatedMember.getAddress());
        return memberRepository.save(existing);
    }

    public Member updateMemberStatus(String id, Member.MemberStatus status) {
        Member member = getMemberById(id);
        member.setStatus(status);
        return memberRepository.save(member);
    }

    public void deleteMember(String id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    public long getTotalMembers() {
        return memberRepository.count();
    }

    // Called by BookIssueService
    public void addActiveIssue(String memberId, String issueId) {
        Member member = getMemberById(memberId);
        if (member.getStatus() == Member.MemberStatus.SUSPENDED) {
            throw new IllegalArgumentException("Member account is suspended: " + memberId);
        }
        member.getActiveIssueIds().add(issueId);
        memberRepository.save(member);
    }

    public void removeActiveIssue(String memberId, String issueId) {
        Member member = getMemberById(memberId);
        member.getActiveIssueIds().remove(issueId);
        memberRepository.save(member);
    }
}
