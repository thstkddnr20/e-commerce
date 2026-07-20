package github.sangwook.ecommerce.member.infrastructure

import github.sangwook.ecommerce.member.domain.Member
import org.springframework.data.repository.CrudRepository

interface MemberRepository: CrudRepository<Member, Long> {

    fun findByEmail(email: String): Member?
}