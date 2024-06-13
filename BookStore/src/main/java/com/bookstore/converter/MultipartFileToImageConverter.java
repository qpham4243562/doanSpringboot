package com.bookstore.converter;

import com.bookstore.entity.Image;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.core.ResolvableType;

@Component
public class MultipartFileToImageConverter implements Converter<MultipartFile, Image> {

    @Override
    public Image convert(MultipartFile file) {
        Image image = new Image();
        try {
            image.setData(file.getBytes());
            image.setType(file.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public ResolvableType getInputType() {
        return ResolvableType.forClass(MultipartFile.class);
    }
}