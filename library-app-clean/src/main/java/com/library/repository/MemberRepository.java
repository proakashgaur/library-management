package com.library.repository;

import com.library.model.Member;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory Member Store — swap with JPA later.
 */
@Repository
public class MemberRepository {

    private final Map<String, Member> store = new ConcurrentHashMap<>();

    public MemberRepository() {
        seedData();
    }

    public Member save(Member member) {
        if (member.getId() == null) {
            member.setId(UUID.randomUUID().toString());
            member.setMemberSince(LocalDateTime.now());
            member.setStatus(Member.MemberStatus.ACTIVE);
            member.setActiveIssueIds(new ArrayList<>());
        }
        member.setUpdatedAt(LocalDateTime.now());
        store.put(member.getId(), member);
        return member;
    }

    public Optional<Member> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Member> findByEmail(String email) {
        return store.values().stream()
                .filter(m -> m.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Member> search(String keyword) {
        String kw = keyword.toLowerCase();
        return store.values().stream()
                .filter(m -> m.getName().toLowerCase().contains(kw)
                        || m.getEmail().toLowerCase().contains(kw)
                        || m.getPhone().contains(kw))
                .collect(Collectors.toList());
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    public void deleteById(String id) {
        store.remove(id);
    }

    public long count() {
        return store.size();
    }

    private void seedData() {
        List<Member> members = List.of(
            Member.builder().id(uuid()).name("Akash Sharma").email("akash.sharma@email.com")
                .phone("9876543210").address("Noida, UP")
                .status(Member.MemberStatus.ACTIVE).activeIssueIds(new ArrayList<>())
                .memberSince(LocalDateTime.now().minusMonths(6)).updatedAt(LocalDateTime.now()).build(),

            Member.builder().id(uuid()).name("Priya Singh").email("priya.singh@email.com")
                .phone("9123456789").address("Delhi, IN")
                .status(Member.MemberStatus.ACTIVE).activeIssueIds(new ArrayList<>())
                .memberSince(LocalDateTime.now().minusMonths(3)).updatedAt(LocalDateTime.now()).build(),

            Member.builder().id(uuid()).name("Rahul Verma").email("rahul.verma@email.com")
                .phone("9988776655").address("Gurgaon, HR")
                .status(Member.MemberStatus.SUSPENDED).activeIssueIds(new ArrayList<>())
                .memberSince(LocalDateTime.now().minusYears(1)).updatedAt(LocalDateTime.now()).build(),

            Member.builder().id(uuid()).name("Ananya Gupta").email("ananya.gupta@email.com")
                .phone("8877665544").address("Noida, UP")
                .status(Member.MemberStatus.ACTIVE).activeIssueIds(new ArrayList<>())
                .memberSince(LocalDateTime.now().minusMonths(2)).updatedAt(LocalDateTime.now()).build(),

            Member.builder().id(uuid()).name("Vikram Patel").email("vikram.patel@email.com")
                .phone("7766554433").address("Mumbai, MH")
                .status(Member.MemberStatus.ACTIVE).activeIssueIds(new ArrayList<>())
                .memberSince(LocalDateTime.now().minusMonths(8)).updatedAt(LocalDateTime.now()).build()
        );

        members.forEach(m -> store.put(m.getId(), m));
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }
}
