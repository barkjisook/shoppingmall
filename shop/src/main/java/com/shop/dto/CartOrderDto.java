package com.shop.dto;   //장바구니 페이지에서 주문할 상품 데이터를 전달할 DTO


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderDto {
    private  Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList; //장바구니에서 여러개의 상품을 주문하므로
}
