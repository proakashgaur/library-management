package com.library.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String address;

    private MemberStatus status;

    @Builder.Default
    private List<String> activeIssueIds = new ArrayList<>();

    private LocalDateTime memberSince;
    private LocalDateTime updatedAt;

    public enum MemberStatus {
        ACTIVE, SUSPENDED, EXPIRED
    }
}
