package com.shop.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter@Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity{
    @Id
    @GeneratedValue
    @Column(name="cart_item_id")
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int count;   //같은상품 몇개담을지 저장

    public static CartItem createCartItem(Cart cart,Item item, int count){  //장바구니에담을상품엔티티를 생성하는 메소드와 장바구니에 담을 수량을 증가시켜주는 메소드
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }
    public void addCount(int count){ //장바구니에 기존에 담겨있는상품인데,해당상품을 추가로 장바구니에 담을때 기존수량에 현재담을수량을 더해줄때 사용하는 메소드
        this.count += count;

}

//현재장바구니에 담겨있는 수량을 변경하는 메소드 추가
public void updateCount(int count){
    this.count = count;
}
}
