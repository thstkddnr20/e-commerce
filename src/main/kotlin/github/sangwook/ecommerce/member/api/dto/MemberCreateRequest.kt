package github.sangwook.ecommerce.member.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class MemberCreateRequest(

    @field:Email
    val email: String,

    @field:Size(min = 8, max = 30, message = "비밀번호는 {min}~{max} 자리 사이여야 합니다.")
    val password: String,

    @field:Size(min = 2, max = 20, message = "이름은 {min}~{max} 자리 사이여야 합니다.")
    val name: String,

)
