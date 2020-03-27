package com.cerner.beadledom.client.resteasy

import com.cerner.beadledom.client.FauxContextListener
import java.io.File
import org.apache.catalina.Globals
import org.apache.catalina.startup.Tomcat
import org.apache.commons.io.FileUtils
import org.apache.naming.resources.VirtualDirContext
import org.scalatest.{BeforeAndAfterAll, Suite}
import scala.collection.immutable.IndexedSeq
import org.scalatest.funspec.AnyFunSpec

/**
 * @author John Leacox
 */
class ServiceBackedSpecSuite extends AnyFunSpec with BeforeAndAfterAll {
  val tomcat = new Tomcat
  val tomcatPort = Integer.parseInt(System.getProperty("tomcat.http.port", "9091"))
  val contextRoot = "/faux-service"

  tomcat.setPort(tomcatPort)
  tomcat.setBaseDir("target/tomcat")
  tomcat.enableNaming()

  FileUtils.forceMkdir(new File("target/tomcat/webapps/faux-service"))
  val context = tomcat.addWebapp(contextRoot, "faux-service")
  val resources = new VirtualDirContext()
  resources.setExtraResourcePaths("/WEB-INF/classes=target/test-classes")
  context.setResources(resources)
  context.setConfigFile(classOf[FauxContextListener].getResource("context.xml"))
  context.getServletContext.setAttribute(Globals.ALT_DD_ATTR, "src/test/webapp/WEB-INF/web.xml")

  tomcat.start()

  override protected def afterAll(): Unit = {
    tomcat.stop()
  }

  val defaultClientHttpEngineSpec = new DefaultClientHttpEngineSpec(contextRoot, tomcatPort)
  val resteasyClientSpec = new ResteasyClientSpec(contextRoot, tomcatPort)

  override def nestedSuites: IndexedSeq[Suite] = {
    Vector(
      defaultClientHttpEngineSpec,
      resteasyClientSpec
    )
  }
}
