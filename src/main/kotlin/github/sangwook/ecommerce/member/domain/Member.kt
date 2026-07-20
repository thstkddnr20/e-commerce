package github.sangwook.ecommerce.member.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "members")
class Member(
    id: Long? = null,
    email: String,
    passwordHash: String,
    name: String,
) {
    @Id
    @Column("id")
    private val id: Long? = id

    @Column("email")
    var email: String = email
        private set

    @Column("password_hash")
    var passwordHash: String = passwordHash
        private set

    @Column("name")
    var name: String = name
        private set

    fun id(): Long {
        return requireNotNull(id) {
            "저장되지 않은 Member의 ID에는 접근할 수 없습니다."
        }
    }
}