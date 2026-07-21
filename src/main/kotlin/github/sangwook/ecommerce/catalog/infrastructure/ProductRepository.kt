package github.sangwook.ecommerce.catalog.infrastructure

import github.sangwook.ecommerce.catalog.api.ProductSummaryResponse
import github.sangwook.ecommerce.catalog.domain.Product
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface ProductRepository: CrudRepository<Product, Long> {

    @Query("""
    SELECT p.id AS id, p.name AS name, MIN(s.price) AS lowest_price
    FROM product p
    JOIN category_closure cc ON p.category_id = cc.descendant
    JOIN sku s ON s.product_id = p.id
    WHERE cc.ancestor = :categoryId
      AND p.status = 'SELLING'
      AND s.status = 'SELLING'
    GROUP BY p.id, p.name
""")
    fun findSellableSummaries(categoryId: Long): List<ProductSummaryResponse>
}