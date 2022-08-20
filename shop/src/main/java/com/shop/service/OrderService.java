package com.shop.service;   //주문 로직 작성을 위해


import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor   //의존성주입 편의성을 위해 사용
public class OrderService {


    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())//주문할 상품을 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); //현재 로그인한 회원의 이메일 정보를 이용해서 회우너정보를 조회한다

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount()); //주문상품 엔티티와 주문수량을 이용하여 주문상품 엔ㅌ티 생성
        orderItemList.add(orderItem);


        Order order = Order.createOrder(member, orderItemList); //회원정보와 주문할 상품 리스트정보를 이용하여 주문 엔티티를 생성
           orderRepository.save(order);  //생성한 주문 엔티티 저장

           return order.getId();
    }
@Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email, pageable); //유저의 아이디와 페이징 조건을 이용하여 주문목록을 조회
        Long totalCount = orderRepository.countOrder(email); //유저의 주문 총 개수를 구한다

    List<OrderHistDto> orderHistDtos = new ArrayList<>();

    for(Order order : orders){  //주문리스트를 순회하면서 구매이력페이지에 전달할 DTO를 생성한다
        OrderHistDto orderHistDto = new OrderHistDto(order);
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem : orderItems){
            ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getId(),"y");//주문한상품의 대표이미지를 조회
            OrderItemDto orderItemDto = new OrderItemDto(orderItem,itemImg.getImgUrl());
            orderHistDto.addOrderItemDto(orderItemDto);

        }
        orderHistDtos.add(orderHistDto);
    }
    return new PageImpl<OrderHistDto>(orderHistDtos,pageable,totalCount); //페이지 구현객체를 생성하여 반환
}
@Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){ //현재로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사를한다.
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember =  order.getMember();
        if(!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            return false;
        }
        return true;
}
public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder(); //주문취소상태로 변경하면 변경감지 기능에 의해서 트랜잭션이 끝날때 업데이트쿼리가 실행된다
}
    public Long orders(List<OrderDto> orderDtoList, String email){

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {  //주문할상품 리스트를 만들어준다
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);  //현재로그인한 회원과 주문상품 목록을이용하여 주문엔티티를 만든다
        orderRepository.save(order); //주문데이터를 저장한다

        return order.getId();
    }

}

