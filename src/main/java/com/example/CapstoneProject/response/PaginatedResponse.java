package com.example.CapstoneProject.response;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private List<T> Response;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int size;
}