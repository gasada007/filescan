package com.example.filescan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class FileProcessorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessorService.class);

	@Value("${files.dir:files}")
	private String dir;

	@Scheduled(fixedDelay = 1000 * 60, initialDelay = 1000 * 30)
	public void processFiles() {
		LOGGER.info("File process starting!");
	}

}
