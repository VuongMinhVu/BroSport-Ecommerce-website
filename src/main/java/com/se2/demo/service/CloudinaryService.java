package com.se2.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
  String uploadFile(MultipartFile file, String folderName);
}
