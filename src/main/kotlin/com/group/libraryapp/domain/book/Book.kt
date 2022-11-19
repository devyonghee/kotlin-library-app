package com.group.libraryapp.domain.book

import javax.persistence.*

@Entity
class Book(
    val name: String,
    @Enumerated(EnumType.STRING)
    val type: BookType,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
        }
    }

    companion object {
        fun fixture(
            name: String = "책이름",
            type: BookType = BookType.COMPUTER,
            id: Long? = null
        ): Book {
            return Book(name, type, id)
        }
    }
}
