package com.stpl.gtn.gtn2o.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

public class GtnFileNameUtils {
	private GtnFileNameUtils() {

	}

	public static String getFullPath(String filePath) {
		return FilenameUtils.getFullPath(filePath);
	}

	public static File getFile(String fileName) {
		return new File(fileName);
	}

	public static String getName(String fileName) {
		return FilenameUtils.getName(fileName);
	}

	public static File getFile(File directory, String fileName) {
		return new File(directory, FilenameUtils.getName(fileName));
	}

	public static FileInputStream getFileInputStream(String path) throws FileNotFoundException {
		return new FileInputStream(path);
	}
	public static Path getPath(String fileName) {
		return Paths.get(fileName);
	}
	public static FileOutputStream getFileOutputStream(String fileName) throws FileNotFoundException {
		return new FileOutputStream(FilenameUtils.getName(fileName));
	}

	public static FileOutputStream getFileOutputStream(File fileName) throws FileNotFoundException {
		return new FileOutputStream(fileName);
	}

	public static FileWriter getFileWriter(String fileName) throws IOException {
		return new FileWriter(fileName);
	}
	public static FileInputStream getFileInputStreamFile(File fileName) throws FileNotFoundException {
		return new FileInputStream(fileName);
	}
}
