package com.ddobang.backend.global.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Ut {
	public static boolean rm(String filePath) throws IOException {
		Path path = Path.of(filePath);

		if (!Files.exists(path))
			return false;

		if (Files.isRegularFile(path)) {
			// 파일이면 바로 삭제
			Files.delete(path);
		} else {
			// 디렉터리면 내부 파일들 삭제 후 디렉터리 삭제
			Files.walkFileTree(path, new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		}

		return true;
	}
}
