package ru.mamapapa.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.mamapapa.property.*;
import ru.mamapapa.dto.NotificationData;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.ConnectException;

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
    private static final String SEND_MESSAGE = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=Markdown";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private Property property;

    public enum PropertyKey implements Key {
        BOT_TOKEN("bot.token"),;

        private final String value;

        PropertyKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public EndpointService() throws Exception {
        initializeProperty();
    }

    private void initializeProperty() throws Exception {
        property = new PropertyService();
        property.load();
    }

    @POST
    @Path("/notify")
    public Response notify(NotificationData data) {
        LOGGER.debug("Получен запрос на уведомление. Входные данные: {}", data);
        try {
            String token = property.getString(PropertyKey.BOT_TOKEN);
            String url = String.format(SEND_MESSAGE, token, data.getUserId(), boldText(data.getHeader()) + "\n" + data.getBody());
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
        return Response.ok().build();
    }

    private String boldText(String text) {
        return String.format("*%s*", text);
    }

    private void throwException(Exception e) {
        LOGGER.error("Не удалось выполнить отправку в телеграм!", e);
        throw new RuntimeException("Не удалось выполнить отправку уведомления в телеграм!");
    }
}
