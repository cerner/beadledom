package com.cerner.beadledom.resteasy

import com.cerner.beadledom.resteasy.fauxservice.FauxContextListener
import java.io.File
import org.apache.catalina.Globals
import org.apache.catalina.startup.Tomcat
import org.apache.commons.io.FileUtils
import org.apache.naming.resources.VirtualDirContext
import org.scalatest._
import scala.collection.immutable.IndexedSeq
import org.scalatest.funspec.AnyFunSpec

/**
 * Suite of tests for Beadledom RestEasy based services.
 */
class ResteasyContextListenerSpecSuite extends AnyFunSpec with BeforeAndAfterAll {

  val tomcat = new Tomcat
  val tomcatPort = Integer.parseInt(System.getProperty("tomcat.http.port", "9091"))
  val contextRoot = "/faux-service"
  val rootUrl = s"http://${tomcat.getHost.getName}:$tomcatPort$contextRoot"

  override def beforeAll(): Unit = {
    tomcat.setPort(tomcatPort)
    tomcat.setBaseDir("target/tomcat")
    tomcat.enableNaming()

    val file = new File("target/tomcat/webapps/faux-service")
    FileUtils.forceMkdir(file)
    val context = tomcat.addWebapp(contextRoot, file.getAbsolutePath)
    val resources = new VirtualDirContext()
    resources.setExtraResourcePaths("/WEB-INF/classes=target/test-classes")
    context.setResources(resources)
    context.setConfigFile(classOf[FauxContextListener].getResource("context.xml"))
    context.getServletContext.setAttribute(Globals.ALT_DD_ATTR, "src/test/webapp/WEB-INF/web.xml")

    tomcat.start()
  }

  override def afterAll(): Unit = {
    tomcat.stop()
  }

  override def nestedSuites: IndexedSeq[Suite] = {
    Vector(
      new BaseContextListenerSpec,
      new ResteasyServiceSpec(rootUrl, tomcatPort)
    )
  }
}
