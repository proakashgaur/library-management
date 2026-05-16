package com.library.service;

import com.library.exception.BookNotAvailableException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.BookIssue;
import com.library.model.Member;
import com.library.repository.BookIssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookIssueService {

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY    = 2.0;   // ₹2/day
    private static final int MAX_BOOKS_PER_MEMBER = 5;

    private final BookIssueRepository issueRepository;
    private final BookService bookService;
    private final MemberService memberService;

    public BookIssue issueBook(String bookId, String memberId) {
        log.info("Issuing book {} to member {}", bookId, memberId);

        Book book = bookService.getBookById(bookId);
        Member member = memberService.getMemberById(memberId);

        if (member.getStatus() == Member.MemberStatus.SUSPENDED) {
            throw new BookNotAvailableException("Member account is suspended");
        }

        if (issueRepository.countActiveByMember(memberId) >= MAX_BOOKS_PER_MEMBER) {
            throw new BookNotAvailableException("Member has reached the maximum limit of " + MAX_BOOKS_PER_MEMBER + " books");
        }

        if (issueRepository.findActiveIssueByMemberAndBook(memberId, bookId).isPresent()) {
            throw new BookNotAvailableException("Member already has this book issued");
        }

        // Decrement available copies (throws if 0)
        bookService.decrementAvailableCopies(bookId);

        BookIssue issue = BookIssue.builder()
                .bookId(bookId)
                .memberId(memberId)
                .bookTitle(book.getTitle())
                .memberName(member.getName())
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS))
                .status(BookIssue.IssueStatus.ISSUED)
                .fineAmount(0.0)
                .build();

        BookIssue saved = issueRepository.save(issue);

        memberService.addActiveIssue(memberId, saved.getId());
        log.info("Book issued. Issue ID: {}", saved.getId());
        return saved;
    }

    public BookIssue returnBook(String issueId) {
        log.info("Processing return for issue ID: {}", issueId);
        BookIssue issue = getIssueById(issueId);

        if (issue.getStatus() == BookIssue.IssueStatus.RETURNED) {
            throw new IllegalArgumentException("Book already returned for issue: " + issueId);
        }

        issue.setReturnDate(LocalDate.now());
        issue.setStatus(BookIssue.IssueStatus.RETURNED);

        // Calculate fine
        if (LocalDate.now().isAfter(issue.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(issue.getDueDate(), LocalDate.now());
            issue.setFineAmount(daysOverdue * FINE_PER_DAY);
            log.info("Overdue by {} days. Fine: ₹{}", daysOverdue, issue.getFineAmount());
        }

        BookIssue saved = issueRepository.save(issue);

        // Update book availability and member's active list
        bookService.incrementAvailableCopies(issue.getBookId());
        memberService.removeActiveIssue(issue.getMemberId(), issueId);

        return saved;
    }

    public BookIssue getIssueById(String id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue record not found with id: " + id));
    }

    public List<BookIssue> getAllIssues() {
        return issueRepository.findAll();
    }

    public List<BookIssue> getIssuesByMember(String memberId) {
        return issueRepository.findByMemberId(memberId);
    }

    public List<BookIssue> getIssuesByBook(String bookId) {
        return issueRepository.findByBookId(bookId);
    }

    public List<BookIssue> getOverdueIssues() {
        return issueRepository.findOverdue();
    }

    public List<BookIssue> getActiveIssues() {
        return issueRepository.findByStatus(BookIssue.IssueStatus.ISSUED);
    }

    public long getTotalIssues() {
        return issueRepository.count();
    }
}
