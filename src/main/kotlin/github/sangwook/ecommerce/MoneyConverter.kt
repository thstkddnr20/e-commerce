package github.sangwook.ecommerce

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class MoneyToIntConverter: Converter<Money, Int>{
    override fun convert(source: Money): Int = source.amount
}

@ReadingConverter
class IntToMoneyConverter: Converter<Int, Money>{
    override fun convert(source: Int): Money = Money(source)
}