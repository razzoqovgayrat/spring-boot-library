package com.library.service;

import com.library.dto.request.BookCopyRequest;
import com.library.dto.request.BookCopyStatusUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BookCopyResponse;
import com.library.dto.response.CreatedResponse;
import com.library.entity.Book;
import com.library.entity.BookCopy;
import com.library.exception.ConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BookCopyService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;


    public ApiResponse<CreatedResponse> create(Long bookId, @Valid BookCopyRequest request) {
        Book book = bookRepository.getByIdAndDeletedAtIsNull(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("book not found"));

        BookCopy bookCopy = BookCopy.builder()
                .book(book)
                .inventoryNumber(request.inventoryNumber())
                .build();
        bookCopyRepository.save(bookCopy);

        return ApiResponse.success(new CreatedResponse(bookCopy.getId()));
    }

    public ApiResponse<Page<BookCopyResponse>> getByBook(Long bookId, Pageable pageable) {
        Book book = bookRepository.getByIdAndDeletedAtIsNull(bookId).orElseThrow(() -> new ResourceNotFoundException("book not found"));
        Page<BookCopy> bookCopies = bookCopyRepository.findAllByBookAndDeletedAtIsNull(book, pageable);

        Page<BookCopyResponse> bookCopyResponses = bookCopies.map(bookCopy ->
                BookCopyResponse.builder()
                        .id(bookCopy.getId())
                        .bookId(book.getId())
                        .bookTitle(book.getTitle())
                        .status(bookCopy.getStatus())
                        .inventoryNumber(bookCopy.getInventoryNumber())
                        .build()
        );

        return ApiResponse.success(bookCopyResponses);
    }

    public ApiResponse<Void> updateStatus(Long id, @Valid BookCopyStatusUpdateRequest request) {
        BookCopy bookCopy = bookCopyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("book copy not found"));

        bookCopy.setStatus(request.status());
        bookCopyRepository.save(bookCopy);

        return ApiResponse.success("Status yangilandi");
    }

    public ApiResponse<Void> delete(Long id) {
        BookCopy bookCopy = bookCopyRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("book copy not found"));

        if (loanRepository.findByCopyId(bookCopy.getId()).isPresent()) {
            throw new ConflictException("This book is on loan and cannot be deleted");
        }

        bookCopy.setDeletedAt(Instant.now());
        bookCopyRepository.save(bookCopy);

        return ApiResponse.success("Nusxa o'chirildi");
    }
}
