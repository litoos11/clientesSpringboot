package com.litoos11.bolsaideas.app.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {

	public String copy(MultipartFile file) throws IOException;
	
	public boolean delete(String fileName);
	
	public void deleteAll();
	
	public void init() throws IOException;
}
