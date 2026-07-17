package com.library.service;

import com.library.dto.request.AuthorRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.AuthorResponse;
import com.library.dto.response.CreatedResponse;
import com.library.entity.Author;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public ApiResponse<CreatedResponse> create(@Valid AuthorRequest request) {
        Author author = Author.builder().fullName(request.fullName()).bio(request.bio()).build();

        author.setCreatedAt(Instant.now());
        authorRepository.save(author);

        return ApiResponse.success(new CreatedResponse(author.getId()));
    }

    @Transactional
    public ApiResponse<AuthorResponse> getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("author not found"));

        return ApiResponse.success(AuthorResponse.fromEntity(author, author.getBooks().size()));
    }

    @Transactional
    public ApiResponse<Page<AuthorResponse>> getAll(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {

            return ApiResponse.success(authorRepository.findAll(pageable)
                    .map(author -> AuthorResponse.fromEntity(author, author.getBooks().size())));
        }

        return ApiResponse.success(authorRepository.findByFullNameContainingIgnoreCase(name, pageable)
                .map(author -> AuthorResponse.fromEntity(author, author.getBooks().size())));
    }

    @Transactional
    public ApiResponse<Void> update(Long id, @Valid AuthorRequest request) {
        Author author = authorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author no found"));

        author.setFullName(request.fullName());
        author.setBio(request.bio());
        author.setUpdatedAt(Instant.now());
        author.setUpdatedBy(customUserDetailsService.getCurrentUser().getUsername());
        authorRepository.save(author);

        return ApiResponse.success("successfully updated");
    }

    public ApiResponse<Void> delete(Long id) {
        Author author = authorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("author not found"));
        author.setDeletedAt(Instant.now());
        authorRepository.save(author);
        return ApiResponse.success("successfully updated");
    }
}
