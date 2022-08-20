package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();  //UUID는 서로다른 개체들을 구별하기 위해서 이름을 부여할때 사용합니다.
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);  //fileData를 파일 출력 스트림에 입력합니다.
        fos.close();
        return savedFileName; //업로드된 파일의 이름을 반환한다.
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath); //파일이 저장된 경로를 이용하여 파일 객체를 생성한다
        if(deleteFile.exists()) {    //해당파일이 존재하면 파일을 삭제한다
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }

}