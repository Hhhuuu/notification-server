package ru.mamapapa;

import javax.ws.rs.container.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static ru.mamapapa.NotificationResponse.failResponse;
import static ru.mamapapa.NotificationResponse.goodResponse;

/**
 * Обертка над ответом
 *
* @author Popov Maxim <m_amapapa@mail.ru>
 */
@Provider
public class NotificationExceptionMapper implements ExceptionMapper<Exception>, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().putSingle("Content-Type", APPLICATION_JSON);
        Object entity = responseContext.getEntity();
        if (entity == null) {
            if (responseContext.getStatus() == 204) {
                responseContext.setStatus(200);
            }
            responseContext.setEntity(goodResponse(""));
            return;
        }

        if (!NotificationResponse.class.isAssignableFrom(entity.getClass())) {
            responseContext.setEntity(goodResponse(entity));
        }
    }

    @Override
    public Response toResponse(Exception e) {
        Error error = new Error(e.getMessage());
        NotificationResponse response = failResponse(error);
        return Response.ok(response).type(APPLICATION_JSON).build();
    }
}
