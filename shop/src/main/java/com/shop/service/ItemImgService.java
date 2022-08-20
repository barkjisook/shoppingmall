package com.shop.service;


import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

   @Value("${itemImgLocation}") //어노테이션을통해 properties파일에 등록한itemImgLocation값을불러와서 변수에 넣어준다
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes()); //
            imgUrl = "/images/item/" + imgName;
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }
public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)
    throws Exception{
        if (!itemImgFile.isEmpty()){ //상품이미지를 수정한경우 상품이지미를 업데이트한다
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId) //상품이미지 아이디를 이용하여 기존에 저장했던 상품이미지 엔티티를 조회한다
                    .orElseThrow(EntityNotFoundException::new);
            //기존이미지 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){ //기존에 등록된상품 이미지파일이 있을경우 해당파일을 삭제
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes()); //업데이트한 상품이미지파일 업로드
            String imgUrl="/images/item/"+imgName;
            savedItemImg.updateItemImg(oriImgName, imgName,imgUrl);//변경된 상품이미지 정보세팅
        }  //SavedItemImg엔티티는 현재영속상태이므로 데이터를 변경하는것만으로도 변경감지 기능이 동작하여 트랜잭션이 끝날때update쿼리가 실행됨
}

}

