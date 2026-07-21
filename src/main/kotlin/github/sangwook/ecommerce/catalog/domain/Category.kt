package github.sangwook.ecommerce.catalog.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("category")
class Category(
    id: Long? = null,
    name: String
) {

    @Id
    @Column("id")
    private val id: Long? = id

    @Column("name")
    var name: String = name
        private set

    fun id(): Long {
        return requireNotNull(id) {
            "저장되지 않은 Category의 ID에는 접근할 수 없습니다."
        }
    }
}
