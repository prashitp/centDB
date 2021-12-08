package com.example.services.logs;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;

public abstract class LogService {
	protected FileWriter fileWriter;
	private String fileName;

	public LogService(String fileName) {
		this.fileName = fileName;
		createFile();
	}

	@SneakyThrows
	private void createFile() {
		File file = new File(fileName);
		file.createNewFile();
		fileWriter = new FileWriter(fileName, true);
	}

	public abstract void log(String string);
}
