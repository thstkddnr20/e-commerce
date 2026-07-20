package github.sangwook.ecommerce.catalog.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("category")
class Category(name: String) {

    @Id
    @Column("category_id")
    val id: Long? = null

    @Column("name")
    var name: String = name
        private set
}
