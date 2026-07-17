package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(name = "uk_books_isbn", columnNames = "isbn")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 20)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "published_year")
    private Integer publishedYear;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookCopy> copies = new ArrayList<>();
}