package ru.mamapapa.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.mamapapa.dto.NotificationData;
import ru.mamapapa.input.FileInput;
import ru.mamapapa.input.Input;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.io.*;
import java.net.ConnectException;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Сервис уведомлений
 *
 * @author Popov Maxim <m_amapapa@mail.ru>
 */
@Controller("notifyEndpointService")
@Component
@Produces(APPLICATION_JSON + "; charset=UTF-8")
@Consumes(APPLICATION_JSON)
@Path("/")
public class EndpointService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointService.class);
    private static final String UTF_8 = "UTF8";
    private static final String SEND_MESSAGE = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=Markdown";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String PROPERTY_FILE_NAME = "application.properties";
    private static Properties properties;

    public enum PropertyKey {
        BOT_TOKEN("bot.token"),
        ;

        private final String value;

        PropertyKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public EndpointService() {
        initializeProperty();
    }

    @POST
    @Path("/notify")
    public Response notify(NotificationData data) {
        LOGGER.debug("Получен запрос на уведомление. Входные данные: {}", data);
        for (String userId : data.getUserIds()) {
            try {
                String token = getProperty(PropertyKey.BOT_TOKEN);
                String url = String.format(SEND_MESSAGE, token, userId, boldText(data.getHeader()) + "\n" + data.getBody());
                REST_TEMPLATE.getForEntity(url, String.class);
            } catch (ResourceAccessException e) {
                if (e.getCause() != null && e.getCause() instanceof ConnectException) {
                    throw new RuntimeException("Превышено время ожидания ответа от телеграма!");
                } else {
                    throwException(e);
                }
            } catch (Exception e) {
                throwException(e);
            }
        }
        return Response.ok().build();
    }

    private String boldText(String text) {
        return String.format("*%s*", text);
    }

    private void throwException(Exception e) {
        LOGGER.error("Не удалось выполнить отправку в телеграм!", e);
        throw new RuntimeException("Не удалось выполнить отправку уведомления в телеграм!");
    }

    public static String getProperty(PropertyKey key) {
        String value = properties.getProperty(key.getValue());
        LOGGER.debug("Получена настройка: {}, значение: {}", key.getValue(), value);
        return value;
    }

    private static void initializeProperty() {
        LOGGER.info("Инициализация файла настроек");
        Input input = new FileInput(PROPERTY_FILE_NAME).withPath("./property");
        try (InputStream inputStream = input.getInputStream()) {
            loadProperty(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить файл настроек!", e);
        }
    }

    private static void loadProperty(InputStream inputStream) {
        properties = new Properties();
        try {
            properties.load(getReader(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить файл настроек!", e);
        }
    }

    private static InputStreamReader getReader(InputStream inputStream) throws UnsupportedEncodingException {
        return new InputStreamReader(inputStream, UTF_8);
    }
}
