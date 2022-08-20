
package com.shop.repository;


import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.shop.dto.MainItemDto;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
//상품조회 조건을 담고 있는 itemSearchDto객체와 페이징 정보를 담고있는 pageable객체를 파라미터로 받는 getAdminItempage메소드를 정의한다.
    //반환데이터로 page<Item>객체를 반환한다.
}