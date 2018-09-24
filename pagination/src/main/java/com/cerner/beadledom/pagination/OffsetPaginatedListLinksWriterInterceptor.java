package com.cerner.beadledom.pagination;

import com.cerner.beadledom.pagination.models.OffsetPaginatedListDto;
import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 * A {@link WriterInterceptor} for adding offset pagination links for {@link OffsetPaginatedList}
 * prior serialization.
 *
 * @author John Leacox
 * @author Will Pruyn
 * @since 3.1
 */
@Provider
public class OffsetPaginatedListLinksWriterInterceptor implements WriterInterceptor {
  @Context
  UriInfo uriInfo;

  @Override
  public void aroundWriteTo(WriterInterceptorContext context)
      throws IOException, WebApplicationException {

    if (context.getEntity() instanceof OffsetPaginatedList) {
      OffsetPaginatedList list = (OffsetPaginatedList) context.getEntity();
      OffsetPaginationLinks links = OffsetPaginationLinks.create(list, uriInfo);

      // Must use raw types because of erasure
      @SuppressWarnings("unchecked")
      OffsetPaginatedListDto listWithLinks = OffsetPaginatedListDtoImpl.builder()
          .items(list.items())
          .totalResults(list.metadata().totalResults())
          .firstLink(links.firstLink())
          .lastLink(links.lastLink())
          .prevLink(links.prevLink())
          .nextLink(links.nextLink())
          .build();

      context.setEntity(listWithLinks);
    }

    context.proceed();
  }
}

