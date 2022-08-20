package com.shop.dto;


import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ItemFormDto {       //상품데이터 정보를 전달하는 Dto

    private Long id;


    @NotBlank(message = "상품명은 필수 입력값입니다.")
    private String itemNm;

    @NotNull(message = "가격은필수값입니다")
    private Integer price;

    @NotBlank(message = "이름은 필수입력값입니다")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력값입니다")
    private Integer stockNumber;


    private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //상품저장후 수정할때 상품 이미지 정보를 저장하는 리스트

    private  List<Long> itemImgIds = new ArrayList<>(); //상품의 이미지 아이디를 저장하는 리스트,수정시에 이미지 아이디를 담아둘 용도로사용

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        return modelMapper.map(this,Item.class); //modelMapper를 이용해서 엔티티객체와DTO객체간의 데이터를 복사하여

    }

    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);  //복사한 객체를 반환해주는 메소드
    }



}
