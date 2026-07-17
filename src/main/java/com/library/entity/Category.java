package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_categories_name", columnNames = "name")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}