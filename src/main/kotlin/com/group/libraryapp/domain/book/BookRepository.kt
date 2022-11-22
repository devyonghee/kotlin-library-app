package com.group.libraryapp.domain.book

import com.group.libraryapp.dto.book.response.BookStatusResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookRepository : JpaRepository<Book, Long> {

    fun findByName(name: String): Book?

    @Query("SELECT new com.group.libraryapp.dto.book.response.BookStatusResponse(b.type, COUNT(b.id))" +
            " FROM Book b GROUP BY b.type")
    fun getStats(): List<BookStatusResponse>
}
