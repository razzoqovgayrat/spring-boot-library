package com.library.dto.response;

import com.library.entity.BookCopy;
import com.library.enums.BookCopyStatus;
import lombok.Builder;

@Builder
public record BookCopyResponse(
        Long id,
        String inventoryNumber,
        BookCopyStatus status,
        Long bookId,
        String bookTitle
) {
    public static BookCopyResponse fromEntity(BookCopy copy) {
        return BookCopyResponse.builder()
                .id(copy.getId())
                .inventoryNumber(copy.getInventoryNumber())
                .status(copy.getStatus())
                .bookId(copy.getBook().getId())
                .bookTitle(copy.getBook().getTitle())
                .build();
    }
}