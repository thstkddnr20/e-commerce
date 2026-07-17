package github.sangwook.ecommerce.member.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "addresses")
class AddressEntity(
    member: MemberEntity,
    recipientName: String,
    recipientPhone: String,
    address: String,
    deliveryRequest: String,
    isDefault: Boolean
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity = member

    @Column(name = "recipient_name", nullable = false)
    var recipientName: String = recipientName
        protected set

    @Column(name = "recipient_phone", nullable = false)
    var recipientPhone: String = recipientPhone
        protected set

    @Column(name = "address", nullable = false)
    var address: String = address
        protected set

    @Column(name = "delivery_request", nullable = false)
    var deliveryRequest: String = deliveryRequest
        protected set

    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean = isDefault
        protected set

}