package com.shop.dto;


import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter@Setter
public class ItemImgDto {

  private Long id;
  private String imgName;
  private String OriImgName;
    private String imgUrl;
    private String repImgYn;

    private static ModelMapper modelMApper = new ModelMapper();

    public static ItemImgDto of(ItemImg itemImg){
        return modelMApper.map(itemImg,ItemImgDto.class);
    }





}
