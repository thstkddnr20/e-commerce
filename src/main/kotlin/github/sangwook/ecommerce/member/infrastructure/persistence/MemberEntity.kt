package github.sangwook.ecommerce.member.infrastructure.persistence

import jakarta.persistence.*

@Entity
@Table(name = "members")
class MemberEntity(email: String, passwordHash: String, name: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(name = "email", unique = true, nullable = false)
    var email: String = email
        protected set

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String = passwordHash
        protected set

    @Column(name = "name", nullable = false)
    var name: String = name
        protected set

}