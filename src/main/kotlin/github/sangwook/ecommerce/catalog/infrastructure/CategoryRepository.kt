package github.sangwook.ecommerce.catalog.infrastructure

import github.sangwook.ecommerce.catalog.domain.Category
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface CategoryRepository: CrudRepository<Category, Long> {

    @Query("SELECT COUNT(*) FROM category_closure WHERE ancestor = :categoryId AND depth > 0")
    fun countDescendants(categoryId: Long): Int

    @Query("SELECT c.id, c.name, cc.ancestor AS parent_id FROM category c LEFT JOIN category_closure cc ON c.id = cc.descendant AND cc.depth = 1")
    fun findAllCategoryFlats(): List<CategoryFlat>
}