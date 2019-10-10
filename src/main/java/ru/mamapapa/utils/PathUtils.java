package ru.mamapapa.utils;

/**
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class PathUtils {
    private PathUtils() {
        throw new UnsupportedOperationException("Вызов конструктора запрешен!");
    }

    public static String addSlashToEnd(String s) {
        String result = s;
        if (result.lastIndexOf('/') != result.length() - 1)
            result += "/";
        return result;
    }

    public static String deleteSlashIfNeed(String path) {
        int length = path.length();
        if (path.lastIndexOf("\\") == length || path.lastIndexOf("/") == length) {
            return path.substring(0, length - 1);
        }
        return path;
    }
}
