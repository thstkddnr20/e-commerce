package github.sangwook.ecommerce.member.api

import github.sangwook.ecommerce.auth.SessionKeys
import github.sangwook.ecommerce.member.api.dto.MemberCreateRequest
import github.sangwook.ecommerce.member.api.dto.MemberLoginRequest
import github.sangwook.ecommerce.member.service.MemberService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/member")
class MemberController(val memberService: MemberService) {

    @PostMapping("/register")
    fun register(@RequestBody request: MemberCreateRequest): ResponseEntity<Any> {
        memberService.register(request.email, request.password, request.name)
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: MemberLoginRequest, session: HttpSession): ResponseEntity<Any> {
        val memberSession = memberService.login(request.email, request.password)
        session.setAttribute(SessionKeys.LOGIN_MEMBER, memberSession)
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        val session = request.getSession(false)
        session.invalidate()
        val cookie = Cookie("JSESSIONID", null)
        cookie.maxAge = 0
        cookie.path = "/"
        response.addCookie(cookie)
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }
}