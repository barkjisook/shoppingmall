package com.shop.dto;    //상세페이지에서 장바구니에 담을 상품의 아이디와 수량을 전달받을 클래스를 생성


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter@Setter
public class CartItemDto {

    @NotNull(message = "상품아이디는 필수값입니다")
    private Long itemId;

    @Min(value = 1,message = "최소1개 이상 담아주세요")
    private int count;

}
