package github.sangwook.ecommerce.member.infrastructure

import github.sangwook.ecommerce.member.infrastructure.persistence.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?
}