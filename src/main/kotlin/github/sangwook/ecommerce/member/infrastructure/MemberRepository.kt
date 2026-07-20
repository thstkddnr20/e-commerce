package github.sangwook.ecommerce.member.infrastructure

import github.sangwook.ecommerce.member.infrastructure.persistence.MemberEntity
import org.springframework.data.repository.CrudRepository

interface MemberRepository: CrudRepository<MemberEntity, Long> {

    fun findByEmail(email: String): MemberEntity?
}