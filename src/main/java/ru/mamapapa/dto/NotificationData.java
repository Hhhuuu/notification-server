package ru.mamapapa.dto;

import java.util.List;

/**
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
public class NotificationData {
    private String header;
    private String body;
    private List<String> userIds;

    public void setHeader(String header) {
        this.header = header;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    @Override
    public String toString() {
        return "{" +
              "header: " + header + " " +
              "body: " + body + " " +
              "userId: " + userIds + " " +
              '}';
    }
}

