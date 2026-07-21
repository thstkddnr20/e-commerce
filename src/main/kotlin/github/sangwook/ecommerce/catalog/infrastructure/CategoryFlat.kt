package github.sangwook.ecommerce.catalog.infrastructure

data class CategoryFlat(
    val id: Long,
    val name: String,
    val parentId: Long?
)