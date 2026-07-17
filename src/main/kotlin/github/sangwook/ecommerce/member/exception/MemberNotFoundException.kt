package github.sangwook.ecommerce.member.exception

class MemberNotFoundException: RuntimeException {
    constructor(message: String) : super(message)
    constructor() : super("이메일 또는 비밀번호가 잘못되었습니다.")
}