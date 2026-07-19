package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);


    // title, authorName, categoryName — har biri mustaqil ixtiyoriy.
    // Hech biri berilmasa — barcha shartlar "true" bo'lib, barcha kitoblar qaytadi.
    // "distinct" kerak: many-to-many join tufayli bitta kitob bir nechta author/category
    // bilan bir nechta marta qatorlanib chiqmasligi uchun.
    @Query(
            value = """
                    select distinct b from Book b
                    left join b.authors a
                    left join b.categories c
                    where b.deletedAt is null
                      and (:title is null or lower(b.title) like lower(concat('%', :title, '%')))
                      and (:authorName is null or lower(a.fullName) like lower(concat('%', :authorName, '%')))
                      and (:categoryName is null or lower(c.name) like lower(concat('%', :categoryName, '%')))
                    """,
            countQuery = """
                    select count(distinct b) from Book b
                    left join b.authors a
                    left join b.categories c
                    where b.deletedAt is null
                      and (:title is null or lower(b.title) like lower(concat('%', :title, '%')))
                      and (:authorName is null or lower(a.fullName) like lower(concat('%', :authorName, '%')))
                      and (:categoryName is null or lower(c.name) like lower(concat('%', :categoryName, '%')))
                    """
    )
    Page<Book> search(
            @Param("title") String title,
            @Param("authorName") String authorName,
            @Param("categoryName") String categoryName,
            Pageable pageable
    );

    Optional<Book> getByIdAndDeletedAtIsNull(Long id);

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
