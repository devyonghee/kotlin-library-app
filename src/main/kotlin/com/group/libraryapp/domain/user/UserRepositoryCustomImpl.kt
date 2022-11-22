package com.group.libraryapp.domain.user

import com.querydsl.jpa.impl.JPAQueryFactory

class UserRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : UserRepositoryCustom {

    override fun findAllWithHistories(): List<User> {
        return queryFactory.selectFrom(QUser.user)
            .distinct()
            .leftJoin(QUser.user.userLoanHistories)
            .fetchJoin()
            .fetch()
    }
}
