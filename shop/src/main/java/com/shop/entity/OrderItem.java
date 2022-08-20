package com.shop.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name="order_id")
    private Order order;

    private int orderPrice;

    private int count;

public  static  OrderItem createOrderItem(Item item,int count){
    OrderItem orderItem = new OrderItem();
    orderItem.setItem(item);   //주문할 상품세팅
    orderItem.setCount(count); //주문수량 세팅
    orderItem.setOrderPrice(item.getPrice()); //가격세팅

    item.removeStock(count);  //주문수량만큼 상품의 재고 수량 감소
    return orderItem;

}
public int getTotalPrice(){      //주문가격과 수량을 곱해서 해당 상품주문가격을 계산하는 메소드
    return  orderPrice*count;
}
public void cancel(){     //주문취소시 주문수량만큼 상품의 재고를 더해준다
    this.getItem().addStock(count);
}

}
