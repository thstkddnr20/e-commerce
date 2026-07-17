package github.sangwook.ecommerce

@JvmInline
value class Money(val amount: Int) {
    init {
        require(amount >= 0) { "금액은 음수일 수 없습니다." }
    }
    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun minus(other: Money) = Money(amount - other.amount)
}