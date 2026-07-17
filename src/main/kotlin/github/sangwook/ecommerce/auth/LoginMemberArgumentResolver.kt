package github.sangwook.ecommerce.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class LoginMemberArgumentResolver: HandlerMethodArgumentResolver {

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val req = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val session = req?.getSession(false)
        return session?.getAttribute(SessionKeys.LOGIN_MEMBER) ?: throw UnauthorizedException()
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasAnnotation = parameter.hasParameterAnnotation(LoginMember::class.java)
        val isMemberSessionType = parameter.parameterType == MemberSession::class.java
        return hasAnnotation && isMemberSessionType
    }
}