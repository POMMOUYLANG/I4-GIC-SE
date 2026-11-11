package edu.itc.shop.api.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

@Provider
@Compress
public class GzipWriterInterceptor implements WriterInterceptor {

    @Context
    private HttpHeaders requestHeaders;

    @Override
    public void aroundWriteTo(WriterInterceptorContext ctx) throws IOException, WebApplicationException {
        String enc = requestHeaders.getHeaderString(HttpHeaders.ACCEPT_ENCODING);
        if (enc != null && enc.toLowerCase().contains("gzip")) {
            ctx.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, "gzip");
            ctx.getHeaders().putSingle(HttpHeaders.VARY, HttpHeaders.ACCEPT_ENCODING);
            ctx.getHeaders().remove(HttpHeaders.CONTENT_LENGTH);

            OutputStream original = ctx.getOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(original);
            ctx.setOutputStream(gzip);
            try {
                ctx.proceed();
            } finally {
                gzip.finish();
            }
        } else {
            ctx.proceed();
        }
    }
}
