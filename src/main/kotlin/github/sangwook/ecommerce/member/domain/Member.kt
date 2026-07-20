package github.sangwook.ecommerce.member.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "members")
class Member(
    email: String,
    passwordHash: String,
    name: String,
) {
    @Id
    @Column("id")
    val id: Long = 0

    @Column("email")
    var email: String = email
        private set

    @Column("password_hash")
    var passwordHash: String = passwordHash
        private set

    @Column("name")
    var name: String = name
        private set

}