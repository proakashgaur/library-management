package com.library.repository;

import com.library.model.BookIssue;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class BookIssueRepository {

    private final Map<String, BookIssue> store = new ConcurrentHashMap<>();

    public BookIssue save(BookIssue issue) {
        if (issue.getId() == null) {
            issue.setId(UUID.randomUUID().toString());
            issue.setCreatedAt(LocalDateTime.now());
        }
        issue.setUpdatedAt(LocalDateTime.now());
        store.put(issue.getId(), issue);
        return issue;
    }

    public Optional<BookIssue> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<BookIssue> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<BookIssue> findByMemberId(String memberId) {
        return store.values().stream()
                .filter(i -> i.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public List<BookIssue> findByBookId(String bookId) {
        return store.values().stream()
                .filter(i -> i.getBookId().equals(bookId))
                .collect(Collectors.toList());
    }

    public List<BookIssue> findByStatus(BookIssue.IssueStatus status) {
        return store.values().stream()
                .filter(i -> i.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<BookIssue> findOverdue() {
        return store.values().stream()
                .filter(i -> i.getStatus() == BookIssue.IssueStatus.ISSUED
                        && i.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public Optional<BookIssue> findActiveIssueByMemberAndBook(String memberId, String bookId) {
        return store.values().stream()
                .filter(i -> i.getMemberId().equals(memberId)
                        && i.getBookId().equals(bookId)
                        && i.getStatus() == BookIssue.IssueStatus.ISSUED)
                .findFirst();
    }

    public long countActiveByMember(String memberId) {
        return store.values().stream()
                .filter(i -> i.getMemberId().equals(memberId)
                        && i.getStatus() == BookIssue.IssueStatus.ISSUED)
                .count();
    }

    public long count() {
        return store.size();
    }
}
