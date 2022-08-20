package com.shop.service;


import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    private final CartService cartService;

    private  final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email) {

        Item item = itemRepository.findById(cartItemDto.getItemId())//장바구니에담을 상품 엔티티를 조회한다
                .orElseThrow(EntityExistsException::new);
        Member member = memberRepository.findByEmail(email);//현재로그인한 회원의 엔티티를 조회한다

        Cart cart = cartRepository.findByMemberID(member.getId());//현재로그인한 회원의 장바구니엔티티를 조회한다
        if (cart == null) { //상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티를 생성한다
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        CartItem savedCartItem =
                cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());//현재상품이 이미 장바구니에 들어있는지 조회

        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount()); //장바구니에 이미있는상품의경우 담을 수량만큼 더해준다
            return savedCartItem.getId();

        } else {
            CartItem cartItem =
                    CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem); //장바구니에 들어갈상품을 저장
            return cartItem.getId();
        }

    }  //로그인한 회원의 저보를 이용하여 장바구니에 들어있는 상품을 조회하는 로직 작성

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberID(member.getId()); //현재로그인한 회원의 장바구니 엔티티를 조회
        if (cart == null) { //장바구니에 상품을 한번도 안담았을경우 장바구니 엔티티가 없으므로 빈 리스트를 반환
            return cartDetailDtoList;
        }
        cartDetailDtoList =
                cartItemRepository.findCartDetailDtoList(cart.getId()); //장바구니에 담겨있는 상품정보를 조회
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email); //현재로그인한 회원을 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember(); //장바구니 상품을 저장한 회원을 조회

        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }    //현재로그인한 회원과 장바구니 상품을 저장한 회원이 다를경우false를 같으면true를 반환

        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count) { //장바구니 상품의 수량을 업데이트하는 메소드
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }
    @DeleteMapping(value = "/cartItem/{cartItemId}") //HTTP의 DELETE
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){


        if(!cartService.validateCartItem(cartItemId, principal.getName())){  //수정권한을 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }
    
    
    //주문로직으로 전다랄 OrderDto리스트 생성및 주문로직 호출, 주문한 상품은 장바구니에서 제거하는 로직구현
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {  
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);  //장바구니에 담은 상품을 주문하도록 주문로직 호출
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {  //주문한 상품을 장바구니에서 제거
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}
