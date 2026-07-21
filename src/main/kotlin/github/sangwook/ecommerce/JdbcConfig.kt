package github.sangwook.ecommerce

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions

@Configuration
class JdbcConfig {
    @Bean
    fun jdbcCustomConversions(): JdbcCustomConversions = JdbcCustomConversions(
        listOf(MoneyToIntConverter(), IntToMoneyConverter())
    )
}