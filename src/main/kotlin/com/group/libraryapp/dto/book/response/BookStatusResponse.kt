package com.group.libraryapp.dto.book.response

import com.group.libraryapp.domain.book.BookType

data class BookStatusResponse(
    val type: BookType,
    val count: Long
)
