package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemImgService itemImgService;

    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품 등록
        Item item = itemFormDto.createItem();  //상품등록폼으로부터 입력받은 데이터를 이용하여item객체를 생성
        itemRepository.save(item); //상품데이터를 저장

        //이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0) 
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i)); //상품의이미지 정보를 저장
        }

        return item.getId();
    }  //상품을 불러오는 메소드
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList=
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);//해당상품의 이미지 조회
        List<ItemImgDto> itemImgDtoList=new ArrayList<>();
        for(ItemImg itemImg : itemImgList){  //조회한 itemImg엔티티를 ItemImgDto객체로 만들어서 리스트에 추가
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        Item item = itemRepository.findById(itemId) //상품의 아이디를 통해 상품엔티티를 조회,
                .orElseThrow(EntityExistsException::new);//없을경우 예외를 발생시킴
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;    //void가 없으면 무조건 return이 필요하다
    }
    public Long updateItem(ItemFormDto itemFormDto,List<MultipartFile> itemImgFileList) throws Exception{
        Item item  = itemRepository.findById(itemFormDto.getId()) //상품등록화면으로부터 전달받은 상품아이디를 이용하여 상품엔티티를 조회
        .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto); //상품등록화면으로부터 전달받은 itemFormDto를 통해 상품 엔티티를 업데이트
        List<Long> itemImgIds = itemFormDto.getItemImgIds(); //상품이미지 아이디 리스트를 조회
        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i)); //상품이미지를 업데이트하기위해서 updateItemImg()메소드에 상품이미지 아이디와,상품이미지 파일정보를 파라미터로 전달
        }
        return  item.getId();
    }
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);

    }
    //메인페이지 보여줄 상품 데이터 조회하는 메소드 추가
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,Pageable pageable){

        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }
}