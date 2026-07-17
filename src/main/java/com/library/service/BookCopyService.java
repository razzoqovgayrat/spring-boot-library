package com.library.service;

import com.library.dto.request.BookCopyRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BookCopyResponse;
import com.library.dto.response.CreatedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookCopyService {
    public ApiResponse<CreatedResponse> create(Long bookId, @Valid BookCopyRequest request) {

        return ApiResponse.success(new CreatedResponse(5));
    }

    public ApiResponse<Page<BookCopyResponse>> getByBook(Long bookId, Pageable pageable) {

        return null;
    }
}
