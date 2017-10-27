#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import com.cerner.beadledom.resteasy.ResteasyContextListener;
import com.google.common.collect.Lists;
import com.google.inject.Module;
import java.util.List;
import javax.servlet.ServletContext;

public class ${name}ContextListener extends ResteasyContextListener {
  @Override
  protected List<? extends Module> getModules(ServletContext context) {
    return Lists.newArrayList(new ${name}Module(), new ResteasyBootstrapModule());
  }
}
