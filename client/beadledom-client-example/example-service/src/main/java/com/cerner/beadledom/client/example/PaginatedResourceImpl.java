package com.cerner.beadledom.client.example;

import com.cerner.beadledom.client.example.model.JsonOne;
import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.jaxrs.GenericResponses;
import com.cerner.beadledom.pagination.OffsetPaginatedList;
import com.cerner.beadledom.pagination.parameters.PaginationParameters;

import com.google.common.collect.Lists;
import java.util.List;
import javax.ws.rs.BeanParam;

/**
 * Service implementation of {@link PaginatedResource}.
 *
 * @author Ian Kottman
 */
public class PaginatedResourceImpl implements PaginatedResource {
  @Override
  public GenericResponse<OffsetPaginatedList<JsonOne>> index(
      @BeanParam PaginationParameters paginationParams) {
    List<JsonOne> items = Lists.newArrayList();
    for (int i = 0; i < 1000; i++) {
      items.add(JsonOne.create("One", "Hello World"));
    }

    OffsetPaginatedList<JsonOne> body = OffsetPaginatedList.<JsonOne>builder()
        .items(items)
        .metadata(paginationParams.getLimit(), paginationParams.getOffset(), 1000L, null)
        .build();

    return GenericResponses.ok(body).build();
  }
}
