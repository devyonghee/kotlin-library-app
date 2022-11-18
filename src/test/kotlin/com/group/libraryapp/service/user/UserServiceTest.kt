package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService
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

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }
}
