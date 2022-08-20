package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg,Long> {


    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);
    //findByItemIdOrderByIdAsc:매개변수로 넘겨준 상품아이디를 가지며,상품이미지 아이디의 오름차순으로 가져오는 쿼리메소드

    ItemImg findByItemIdAndRepimgYn(Long itemId,String repimgYn);
}
