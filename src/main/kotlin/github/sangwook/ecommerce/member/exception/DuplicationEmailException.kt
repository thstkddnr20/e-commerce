package github.sangwook.ecommerce.member.exception

class DuplicationEmailException: RuntimeException {
    constructor(message: String) : super(message)
}