package github.sangwook.ecommerce.catalog.infrastructure

import github.sangwook.ecommerce.catalog.domain.SaleStatus
import github.sangwook.ecommerce.catalog.domain.Sku
import org.springframework.data.repository.CrudRepository

interface SkuRepository: CrudRepository<Sku, Long>{
    fun findByProductId(productId: Long): List<Sku>

    fun existsByProductIdAndOptionName(productId: Long, optionName: String): Boolean

    fun countByProductIdAndStatus(productId: Long, status: SaleStatus): Int
}