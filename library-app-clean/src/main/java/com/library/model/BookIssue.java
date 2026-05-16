package com.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookIssue {

    private String id;
    private String bookId;
    private String memberId;
    private String bookTitle;    // denormalized for quick display
    private String memberName;   // denormalized for quick display

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private IssueStatus status;

    private Double fineAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum IssueStatus {
        ISSUED, RETURNED, OVERDUE, LOST
    }
}
