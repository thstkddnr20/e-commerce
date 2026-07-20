package github.sangwook.ecommerce.member.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "members")
data class MemberEntity(
    @Id
    val id: Long = 0,

    @Column("email")
    var email: String,

    @Column("password_hash")
    var passwordHash: String,

    @Column("name")
    var name: String
)