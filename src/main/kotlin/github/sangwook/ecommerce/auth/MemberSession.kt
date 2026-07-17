package github.sangwook.ecommerce.auth

data class MemberSession(
    val userId: Long,
    val email: String,
    val name: String,
)