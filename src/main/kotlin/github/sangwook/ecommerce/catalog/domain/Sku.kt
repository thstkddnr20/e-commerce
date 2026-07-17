package github.sangwook.ecommerce.catalog.domain

import github.sangwook.ecommerce.Money

class Sku(
    val id: Long,
    val optionName: String,
    var price: Money,
    var status: SaleStatus
)