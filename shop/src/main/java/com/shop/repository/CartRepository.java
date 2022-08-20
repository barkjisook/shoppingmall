package com.shop.repository;

import com.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartRepository extends JpaRepository<Cart,Long>{


    Cart findByMemberID(Long memberId); //로그인한 회원의 cart엔티티를 찾기위한 쿼리 메소드
}
