package com.shop.controller;  //주문 관련 요청 처리


import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
                                              Principal principal) {  //스프링에서 비동기 처리할때 @Requestbody,@ResponseBody사용
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST); //에러정보를 ResponseEntity에 담아서 반환
        }
        String email = principal.getName(); //principal에서 현재로그인한 회원의 이메일 정보를 조회한다
        Long orderId;
        try {
            orderId = orderService.order(orderDto, email); //넘어오는 주문정보와 회원의 이메일 정보를 이용하여 주문 로직 호출
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK); //결과값으로 생성된 주문 번호와 요청이 성공했다는 HTTP응답상태를 코드로 반환
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4); //한번에 가지고올 주문의 개수는 4개로 설정
        Page<OrderHistDto> ordersHistDtoList =
                orderService.getOrderList(principal.getName(), pageable);
//현재로그인한 회원은 이메일과 페이징객체를 파라미터로 전달하여 화면에 전달한 주문 목록데이터를 리턴값으로 받는다
        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder
            (@PathVariable("orderId") Long orderId, Principal principal) {
        if (!orderService.validateOrder(orderId, principal.getName())) { //자바스크립트에서 취소할 주문번호는 조작이 가능하므로 다른사람의 주문을 취소하지못하도록 주문 취소권한 검사를 한다
            return new ResponseEntity<String>("주문취소 권한이 없습니다", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId); //주문취소로직을 호출한다
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}