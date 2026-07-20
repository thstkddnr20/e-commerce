package github.sangwook.ecommerce.member.service

import github.sangwook.ecommerce.auth.MemberSession
import github.sangwook.ecommerce.member.exception.DuplicationEmailException
import github.sangwook.ecommerce.member.exception.MemberNotFoundException
import github.sangwook.ecommerce.member.infrastructure.MemberRepository
import github.sangwook.ecommerce.member.infrastructure.persistence.MemberEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(email: String, password: String, name: String) {
        val member = memberRepository.findByEmail(email)
        if (member != null) {
            throw DuplicationEmailException("이메일이 이미 존재합니다.")
        }
        val memberEntity = MemberEntity(email = email, passwordHash = passwordEncoder.encode(password)!!, name = name)
        memberRepository.save(memberEntity)
    }

    fun login(email: String, rawPassword: String): MemberSession {
        val member = memberRepository.findByEmail(email) ?: throw MemberNotFoundException()
        if (!passwordEncoder.matches(rawPassword, member.passwordHash))
            throw MemberNotFoundException()
        return MemberSession(member.id, member.email, member.name)
    }

}