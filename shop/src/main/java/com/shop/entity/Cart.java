package com.shop.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="cart")
@Getter@Setter
@ToString
public class Cart extends BaseEntity{

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
private Long id;

@OneToOne (fetch = FetchType.LAZY)  //회원 엔티티와 일대일로 매핑을 한다
@JoinColumn(name="member_id"   )  //외래키를 지정한다.
    private Member member;


public static Cart createCart(Member member) {
    Cart cart = new Cart();
    cart.setMember(member);
    return cart;
}
}