package com.searcher.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

import static com.searcher.util.ZipUtils.unpack;

@Service
public class ZipService {
    public List<String> findPasswords(String fileName) {
        Resource resource = new ClassPathResource("sb.tar");
        try {
            InputStream resourceInputStream = resource.getInputStream();
            unpack(resourceInputStream.readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
