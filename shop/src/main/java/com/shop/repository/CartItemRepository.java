package com.shop.repository;  //장바구니에 들어갈 상품을 저장하거나 조회하기 위해서 생성함

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
//카트 아이디와 상품 아이디를 이용해서 상품이 장바구니에 들어있는지 조회한다
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +  //장바구니에 담겨있는 상품의 대표 이미지만 가지고 오도록 조건문
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

}

/*@Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
          "from CartItem ci, ItemImg im " +

       CartDetailDto의 생성자를 이용하여 DTO를 반환할때는 "new com.shop.CartDetailDto(ci,id,i,itemNm,i.price,
       ci,ci.count,im.imgUrl)처럼 new키워드와 해당DTO패키지,클래스명을 적어준다.
       또한, 생성자의 파라미터 순서는 DTO클래스에 명시한 순으로 넣어줘야한다.


 */