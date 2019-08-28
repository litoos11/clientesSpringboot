package com.litoos11.bolsaideas.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.litoos11.bolsaideas.app.controller.ClienteController;
import com.litoos11.bolsaideas.app.service.IUploadFileService;


@Service("uploadFileServiceImpl")
public class UploadFileServiceImpl implements IUploadFileService {

	private static final String UPLOADS_FOLDER = "uploads";
	private static final Log LOG = LogFactory.getLog(ClienteController.class);

	@Override
	public String copy(MultipartFile file) throws IOException {
		// TODO Auto-generated method stub
		String uniqueFileName = UUID.randomUUID().toString() + " " + file.getOriginalFilename();
		Path rootAbsolutePath = getPath(uniqueFileName);

		LOG.info("rootAbsolutePath: " + rootAbsolutePath);

		Files.copy(file.getInputStream(), rootAbsolutePath);

		return uniqueFileName;
	}

	@Override
	public boolean delete(String fileName) {
		// TODO Auto-generated method stub
		Path rootPath = getPath(fileName);
		File archivo = rootPath.toFile();
		
		if(archivo.exists() && archivo.canRead()) {
			if(archivo.delete()) {
				return true;
			}
		}
		return false;
	}

	public Path getPath(String fileName) {
		return Paths.get(UPLOADS_FOLDER).resolve(fileName).toAbsolutePath();
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
	}

	@Override
	public void init() throws IOException{
		// TODO Auto-generated method stub
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));
	}

}
