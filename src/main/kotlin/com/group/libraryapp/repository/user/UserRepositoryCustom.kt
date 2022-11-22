package com.group.libraryapp.repository.user

import com.group.libraryapp.domain.user.QUser
import com.group.libraryapp.domain.user.User
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class UserRepositoryCustom(
    private val queryFactory: JPAQueryFactory
) {

    fun findAllWithHistories(): List<User> {
        return queryFactory.selectFrom(QUser.user)
            .distinct()
            .leftJoin(QUser.user.userLoanHistories)
            .fetchJoin()
            .fetch()
    }
}
