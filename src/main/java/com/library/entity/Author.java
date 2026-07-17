package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author extends BaseEntity {

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     *     Many-to-many tanlandi (1-to-many emas): bitta kitobning bir nechta
     *     muallifi bo'lishi odatiy holat (hammuallif kitoblar), va bitta muallif
     *     bir nechta kitob yozgan bo'ladi. Egalik (owning side) Book tomonida —
     *     chunki kitob yaratilganda "qaysi mualliflarga tegishli" belgilanadi,
     *     muallif tomonidan emas.
     */

    @ManyToMany(mappedBy = "authors")
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}