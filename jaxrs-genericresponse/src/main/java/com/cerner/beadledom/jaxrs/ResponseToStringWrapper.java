package com.cerner.beadledom.jaxrs;

import javax.ws.rs.core.Response;

/**
 * Simple class to implement {@link Response} to String
 */
public class ResponseToStringWrapper {

    /**
     * Converts {@link Response} to String
     *
     * @param response
     *      the {@link Response} to convert
     * @return {@link Response} as a String
     */
    public static String toString(Response response) {
        return "Response{status=" + response.getStatus() + ", mediaType="
                + response.getMediaType() + ", date=" + response.getDate() + ", length=" + response.getLength()
                + ", lastModified=" + response.getLastModified() + ", entityTag=" + response.getEntityTag()
                + ", language=" + response.getLanguage() + ", location=" + response.getLocation()
                + ", headers=" + response.getHeaders() + ", cookies=" + response.getCookies() + ", links="
                + response.getLinks() + " }";
    }
}
