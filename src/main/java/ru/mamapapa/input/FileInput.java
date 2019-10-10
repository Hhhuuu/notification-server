package ru.mamapapa.input;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.mamapapa.utils.PathUtils.addSlashToEnd;
/**
 * Ввод из файла
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class FileInput implements Input {
    private final String fileName;
    private String path;

    public FileInput(String fileName) {
        this.fileName = fileName;
        this.path = "";
    }

    public FileInput withPath(String path) {
        this.path = addSlashToEnd(path);
        return this;
    }

    private String getUrl() {
        return path + fileName;
    }

    @Override
    public InputStream getInputStream() {
        InputStream inputStream = null;
        Path localPath = Paths.get(getUrl());
        if (localPath.toFile().exists()) {
            try {
                inputStream = new FileInputStream(localPath.toString());
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Отсутствует файл: " + localPath, e);
            }
        } else {
            inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        }
        if (inputStream == null)
            throw new IllegalArgumentException("Отсутствует файл: " + localPath);
        return inputStream;
    }
}
