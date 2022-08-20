package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType;   //현재시간과 상품등록일을 비교해서 상품데이터를 조회

    private ItemSellStatus searchSellStatus; //상품의 판매상태를 기준으로 상품데이터를 조회

    private String searchBy; //상품을 조회할때 어떤유형으로 조회할지 선택

    private String searchQuery = "";  //조회할검색어 저장변수

}