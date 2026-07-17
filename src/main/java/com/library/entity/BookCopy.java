package com.library.entity;

import com.library.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_copies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_book_copies_inventory_number", columnNames = "inventory_number")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookCopy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "inventory_number", nullable = false, length = 50)
    private String inventoryNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BookCopyStatus status = BookCopyStatus.AVAILABLE;

    // Diqqat: alohida "version" field yozmadim — BaseEntity'da @Version
    // allaqachon bor. Talabnomada "BookCopy'da @Version" deyilgani —
    // aynan shu meros orqali kelayotgan versionni nazarda tutadi,
    // qayta e'lon qilish shart emas (aks holda ikkita @Version xato beradi).
}