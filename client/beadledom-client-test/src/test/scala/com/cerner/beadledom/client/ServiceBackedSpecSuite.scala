package com.cerner.beadledom.client

import com.cerner.beadledom.client.example.FauxContextListener

import org.apache.catalina.Globals
import org.apache.catalina.startup.Tomcat
import org.apache.commons.io.FileUtils
import org.apache.naming.resources.VirtualDirContext
import org.scalatest.{BeforeAndAfterAll, FunSpec, Suite}

import java.io.File

import scala.collection.immutable.IndexedSeq

/**
  * Spec Suite to test the Clients of a service.
  */
class ServiceBackedSpecSuite extends FunSpec with BeforeAndAfterAll {
  val tomcat = new Tomcat
  val tomcatPort = Integer.parseInt(System.getProperty("tomcat.http.port", "9091"))
  val contextRoot = "/faux-service"
  val examplePath = "../beadledom-client-example/example-service"

  tomcat.setPort(tomcatPort)
  tomcat.setBaseDir(s"$examplePath/target/tomcat")
  tomcat.enableNaming()
  val connector = tomcat.getConnector
  connector.setProperty("connectionTimeout", String.valueOf(-1))

  tomcat.setConnector(connector)

  FileUtils.forceMkdir(new File(s"$examplePath/target/tomcat/webapps/faux-service"))
  val context = tomcat.addWebapp(contextRoot, "faux-service")
  val resources = new VirtualDirContext()
  resources.setExtraResourcePaths("/WEB-INF/classes=target/test-classes")
  context.setResources(resources)
  context.setConfigFile(classOf[FauxContextListener].getResource("context.xml"))
  context.getServletContext
      .setAttribute(Globals.ALT_DD_ATTR, s"$examplePath/src/main/webapp/WEB-INF/web.xml")

  tomcat.start()

  override protected def afterAll(): Unit = {
    tomcat.stop()
  }

  val clientServiceSpec = new ClientServiceSpec(contextRoot, tomcatPort)

  override def nestedSuites: IndexedSeq[Suite] = {
    Vector(
      clientServiceSpec
    )
  }
}
