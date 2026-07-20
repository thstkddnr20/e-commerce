package github.sangwook.ecommerce.member.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("addresses")
class Address(
    memberId: Long,
    recipientName: String,
    recipientPhone: String,
    address: String,
    deliveryRequest: String,
    isDefault: Boolean
) {
    @Id
    @Column("id")
    val id: Long = 0

    @Column("member_id")
    val memberId: Long = memberId

    @Column("recipient_name")
    val recipientName: String = recipientName

    @Column("recipient_phone")
    val recipientPhone: String = recipientPhone

    @Column("address")
    val address: String = address

    @Column("delivery_request")
    val deliveryRequest: String = deliveryRequest

    @Column("is_default")
    val isDefault: Boolean = isDefault
}