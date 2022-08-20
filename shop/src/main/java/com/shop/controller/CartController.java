package com.shop.controller;


import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.entity.CartItem;
import com.shop.service.CartService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping(value="/cart")
public @ResponseBody
    ResponseEntity order(@RequestBody@Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName(); //현재로그인한 회원의 이메일 저보를 변수에 저장
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);//장바구니에 담을 상품정보와 현재 로그인한 회원의 이메일 정보를 이용하여 장바구니에 상품을 담는 로직을 호출
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);//결과값으로 생성된 장바구니 상품 아이디와 요청이 성공하였다는 HTTP응답상태 코드를 반환
    }
 //장바구니 페이지로 이동
@GetMapping(value="/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailList =
                cartService.getCartList(principal.getName()); //현재로그인한 사용자의 이메일 정보를 이용하여 장바구니에 담겨있는 상품정보를 조회
    model.addAttribute("cartItems",cartDetailList); //조회한 장바구니 상품정보를 뷰로 전달
    return "cart/cartList";
}

//장바구니 상품의수량 업데이트, Http메소드에서 PAtch는 요정된자원의 일부를 업데이트할때 사용하기때문에
    @PatchMapping(value="/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem
    (@PathVariable("cartItemId")Long cartItemId, int count, Principal principal){
        if(count <= 0 ){
            return new ResponseEntity<String>
                    ("최소 한개이상 담아주세요", HttpStatus.BAD_REQUEST);

        } else if (!cartService.validateCartItem(cartItemId, principal.getName())) { //수정권한을 체크한다
            return new ResponseEntity<String>
                    ("수정권한이 없습니다",HttpStatus.FORBIDDEN);
        }
        cartService.updateCartItemCount(cartItemId,count);//장바구니상품의 개수를 업데이트
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){  //주문할 상품을 선택하지 않았는지 체크
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {  //주문권한체크
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){  //주문번호반환
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN); //생성된주문번호와 요청이 성공했다는응답HTTP응답상태 반환
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
