package com.group.libraryapp.service.book

import com.group.libraryapp.domain.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @Test
    @DisplayName("책을 등록")
    fun saveBook() {
        //given
        val bookName = "이상한 나라의 엘리스"
        val request = BookRequest(bookName)
        //when
        bookService.saveBook(request)
        //then
        assertThat(bookRepository.findAll())
            .hasSize(1)
            .first()
            .extracting(Book::name)
            .isEqualTo(bookName)
    }

    @Test
    @DisplayName("책을 대출")
    fun loanBook() {
        //given
        val userName = "한용희"
        val bookName = "이상한 나라의 엘리스"
        bookRepository.save(Book(bookName))
        val user = userRepository.save(User(userName, null))
        //when
        bookService.loanBook(BookLoanRequest(userName, bookName))
        //then
        assertThat(userLoanHistoryRepository.findAll())
            .hasSize(1)
            .first()
            .satisfies {
                assertThat(it.bookName).isEqualTo(bookName)
                assertThat(it.user.id).isEqualTo(user.id)
                assertThat(it.isReturn).isFalse()
            }
    }

    @Test
    @DisplayName("책이 대출되어 있다면 대출 실패")
    fun loanBook_alreadyLoan_thrownIllegalArgumentException() {
        //given
        val bookName = "이상한 나라의 엘리스"
        val userName = "한용희"
        bookRepository.save(Book(bookName))
        val user = userRepository.save(User(userName, null))
        userLoanHistoryRepository.save(UserLoanHistory(user, bookName, false))
        //when, then
        assertThatIllegalArgumentException().isThrownBy {
            bookService.loanBook(BookLoanRequest(userName, bookName))
        }.withMessage("진작 대출되어 있는 책입니다")
    }

    @Test
    @DisplayName("책을 반납")
    fun returnBook() {
        //given
        val bookName = "이상한 나라의 엘리스"
        val userName = "한용희"
        bookRepository.save(Book(bookName))
        val user = userRepository.save(User(userName, null))
        userLoanHistoryRepository.save(UserLoanHistory(user, bookName, false))
        //when
        bookService.returnBook(BookReturnRequest(userName, bookName))
        //then
        assertThat(userLoanHistoryRepository.findAll())
            .hasSize(1)
            .first()
            .extracting(UserLoanHistory::isReturn)
            .isEqualTo(true)
    }

    @AfterEach
    fun tearDown() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
        userLoanHistoryRepository.deleteAll()
    }
}
