package com.cerner.beadledom.jaxrs;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.function.Consumer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

/**
 * A streaming output for {@link OutputStreamWriter}s.
 *
 * @see javax.ws.rs.core.StreamingOutput
 *
 * @author John Leacox
 */
public class StreamingWriterOutput implements StreamingOutput {
  private final Consumer<OutputStreamWriter> consumer;

  private StreamingWriterOutput(Consumer<OutputStreamWriter> consumer) {
    this.consumer = checkNotNull(consumer, "consumer:null");
  }

  /**
   * Creates a new {@link StreamingWriterOutput} that will execute the provided function when
   * writing the output. The {@code writer} provided to the function will use the UTF-8 charset.
   */
  public static StreamingWriterOutput with(Consumer<OutputStreamWriter> consumer) {
    return new StreamingWriterOutput(consumer);
  }

  @Override
  public void write(OutputStream output) throws IOException, WebApplicationException {
    OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
    consumer.accept(writer);
    writer.flush();
  }
}
