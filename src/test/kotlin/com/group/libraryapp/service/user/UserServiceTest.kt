package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.dto.user.response.BookHistoryResponse
import com.group.libraryapp.dto.user.response.UserLoanHistoryResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository
) {
    @Test
    fun saveUser() {
        //given
        val request = UserCreateRequest("한용희", null)
        //when
        userService.saveUser(request)
        //then
        assertThat(userRepository.findAll())
            .hasSize(1)
            .first()
            .satisfies {
                assertThat(it.name).isEqualTo("한용희")
                assertThat(it.age).isNull()
            }
    }

    @Test
    fun getUsersTest() {
        //given
        userRepository.saveAll(
            listOf(
                User("A", 20),
                User("B", null)
            )
        )
        //when
        val users = userService.getUsers()
        //then
        assertThat(users)
            .hasSize(2)
            .extracting("name", "age")
            .containsAnyOf(
                tuple("A", 20),
                tuple("B", null)
            )
    }

    @Test
    fun updateUserName() {
        //given
        val user = userRepository.save(User("A", 20))
        val updatedName = "B"
        //when
        userService.updateUserName(UserUpdateRequest(user.id!!, updatedName))
        //then
        assertThat(userRepository.findById(user.id))
            .get()
            .extracting(User::name)
            .isEqualTo(updatedName)
    }

    @Test
    fun deleteUser() {
        //given
        userRepository.save(User("A", null))
        //when
        userService.deleteUser("A")
        //then
        assertThat(userRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("대출 기록이 없는 유저도 응답에 포함된다")
    fun getUserLoanHistoriesTest1() {
        //given
        userRepository.save(User("A", null))
        //when
        val results = userService.getUserLoanHistories()
        //then
        assertThat(results)
            .hasSize(1)
            .first()
            .extracting(UserLoanHistoryResponse::name, UserLoanHistoryResponse::books)
            .isEqualTo(listOf("A", emptyList<BookHistoryResponse>()))
    }

    @Test
    @DisplayName("대출 기록이 많은 유저 응답이 작동된다")
    fun getUserLoanHistoriesTest2() {
        //given
        val savedUser = userRepository.save(User("A", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(savedUser, "책3", UserLoanStatus.RETURNED)
            )
        )
        //when
        val results = userService.getUserLoanHistories()
        //then
        assertThat(results)
            .hasSize(1)
            .first()
            .extracting(UserLoanHistoryResponse::books)
            .isEqualTo(
                listOf(
                    BookHistoryResponse("책1", false),
                    BookHistoryResponse("책2", false),
                    BookHistoryResponse("책3", true)
                )
            )
    }

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }
}
