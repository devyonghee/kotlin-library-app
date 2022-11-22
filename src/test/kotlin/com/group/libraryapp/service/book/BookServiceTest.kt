package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatusResponse
import org.assertj.core.api.Assertions.*
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
        val bookType = BookType.COMPUTER
        val request = BookRequest(bookName, bookType)
        //when
        bookService.saveBook(request)
        //then
        assertThat(bookRepository.findAll())
            .hasSize(1)
            .first()
            .extracting(Book::name, Book::type)
            .isEqualTo(listOf(bookName, bookType))
    }

    @Test
    @DisplayName("책을 대출")
    fun loanBook() {
        //given
        val userName = "한용희"
        val bookName = "이상한 나라의 엘리스"
        bookRepository.save(Book.fixture(bookName))
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
                assertThat(it.status).isEqualTo(UserLoanStatus.LOANED)
            }
    }

    @Test
    @DisplayName("책이 대출되어 있다면 대출 실패")
    fun loanBook_alreadyLoan_thrownIllegalArgumentException() {
        //given
        val bookName = "이상한 나라의 엘리스"
        val userName = "한용희"
        bookRepository.save(Book.fixture(bookName))
        val user = userRepository.save(User(userName, null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(user, bookName))
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
        bookRepository.save(Book.fixture(bookName))
        val user = userRepository.save(User(userName, null))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(user, bookName))
        //when
        bookService.returnBook(BookReturnRequest(userName, bookName))
        //then
        assertThat(userLoanHistoryRepository.findAll())
            .hasSize(1)
            .first()
            .extracting(UserLoanHistory::status)
            .isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("책 대여 권수를 정상 확인")
    fun countLoanedBook() {
        //given
        val savedUser = userRepository.save(User("한용희", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "A"),
                UserLoanHistory.fixture(savedUser, "B", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(savedUser, "C", UserLoanStatus.RETURNED)
            )
        )
        //when
        val result = bookService.countLoanedBook()
        //then
        assertThat(result).isEqualTo(1)
    }

    @Test
    @DisplayName("분야별 책 권수를 정상 확인")
    fun getBookStatistics() {
        //given
        bookRepository.saveAll(
            listOf(
                Book.fixture("A", BookType.COMPUTER),
                Book.fixture("B", BookType.COMPUTER),
                Book.fixture("C", BookType.SCIENCE)
            )
        )
        //when
        val result = bookService.getBookStatistics()
        //then
        assertThat(result)
            .hasSize(2)
            .extracting(BookStatusResponse::type, BookStatusResponse::count)
            .containsAnyOf(
                tuple(BookType.COMPUTER, 2),
                tuple(BookType.SCIENCE, 1),
            )
    }

    @AfterEach
    fun tearDown() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
        userLoanHistoryRepository.deleteAll()
    }
}
