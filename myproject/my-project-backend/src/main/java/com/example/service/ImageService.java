package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.StoreImage;
import com.example.mapper.ImageStoreMapper;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ImageService extends IService<StoreImage> {
    String uploadImage(MultipartFile file, int id) throws IOException;
    String uploadAvatar(MultipartFile file, int id) throws IOException;
    void fetchImageFromMinio(OutputStream stream, String image) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
