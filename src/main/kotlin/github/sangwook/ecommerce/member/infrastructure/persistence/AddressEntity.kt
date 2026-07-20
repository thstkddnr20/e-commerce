package github.sangwook.ecommerce.member.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("addresses")
data class AddressEntity(
    @Id
    val id: Long = 0,

    val memberId: Long,

    @Column("recipient_name")
    val recipientName: String,

    @Column("recipient_phone")
    val recipientPhone: String,

    val address: String,

    @Column("delivery_request")
    val deliveryRequest: String,

    @Column("is_default")
    val isDefault: Boolean
)