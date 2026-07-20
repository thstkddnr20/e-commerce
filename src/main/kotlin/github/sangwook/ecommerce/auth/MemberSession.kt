package github.sangwook.ecommerce.auth

data class MemberSession(
    val memberId: Long,
    val email: String,
    val name: String,
)