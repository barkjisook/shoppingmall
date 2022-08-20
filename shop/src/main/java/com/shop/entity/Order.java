package com.shop.entity;


import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallback;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
@Table(name="orders")
public class Order {
    
    
    
    @Id
    @Column(name="order_id")
    private Long id;
    
    
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;         //한면의 회원이 여러버 주문 할수 있으므로 주문엔티티 기준에서 다대일 단방향매핑ㄴ
    
    private LocalDateTime orderDate; //주문일
    
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
    orphanRemoval = true,fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime regTime;
    private LocalDateTime updateTime;


    public void addOrderItem(OrderItem orderItem){ //orderltems에는 주문상품정보담아줌.orderItem객체를 order객체의 orderItems에 추가한다
        orderItems.add(orderItem);
        orderItem.setOrder(this); //orderitem객체에 order객체를 세팅
    }
    public static Order createOrder(Member member,List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);  //상품을 주문한 회원의 정보를 세팅
        for(OrderItem orderItem : orderItemList){ //장바구니는 여러개의 상품을 담을수있게 리스트형태로 파라미터값을 받으며 주문객체에 orderItem객체를 추가
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER); //주문상태를ORDER로 세팅
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    public int getTotalPrice(){  //총주문금액 구하는 메소드
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
    public void cancelOrder(){    //주문상태를 취소상태로 바꿔주는 메소드 구현
        this.orderStatus = OrderStatus.CANCLE; //constant파일에 만든거 잊지말자!!!

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }

    }
}
