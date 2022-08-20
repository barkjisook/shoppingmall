package com.shop.controller;


import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Controller
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {

        if (bindingResult.hasErrors()) {  //상품등록시 필수값이 없다면 상품등록페이지로 전환한다
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId); //조회한 상품데이터를 모델에 담아서 뷰로전달
            model.addAttribute("itemFormDto", itemFormDto);


        } catch (EntityNotFoundException e) {  //상품엔티티가 존재하지 않을경우 에러메시지를 담아서 상품등록페이지로 이동
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다"); //변수이름, 변수에 넣을데이터값
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }
@PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,BindingResult bindingResult,@RequestParam("itemImgFile")
                             List<MultipartFile>itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errormessage","첫번째상품이미지는필수값입니다");
            return "item/itemForm";
        }
        try {
            itemService.updateItem(itemFormDto, itemImgFileList); //상품수정로직을 호출

        }catch(Exception e){
        model.addAttribute("errormessage","상품수정중 에러가 발생했습니다");
        return "item/itemForm";


    }
        return "redirect/";
}
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"}) //URL에 페이지가 있는경우와 없는경우 둘다매핑
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3); //첫번쨰는 조회할 페이지번호, 두번째는 한번에 가지고올 데이터수
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable); //조회조건과 페이징 정보를 파라미터로 넘겨서 Page<Item>객체를 반환받는다
        model.addAttribute("items", items); //조회한 상품 데이터및 페이징 정보를 뷰에 전달
        model.addAttribute("itemSearchDto", itemSearchDto); //페이지 전환시 기존 검색조건을 유지한채 이동할수 있도록 뷰에 다시전달
        model.addAttribute("maxPage", 5); //상품관리 메뉴하단에 보여줄 페이지 번호의 최대개수

        return "item/itemMng";
    }
    @GetMapping(value="/item/{itemId}")
    public String itemDtl(Model model,@PathVariable("itemId")Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item",itemFormDto);
        return "item/itemDtl";
    }
    }

