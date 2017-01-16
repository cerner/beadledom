package com.cerner.beadledom.resteasy.fauxservice.dao;

public class HelloDao {
  public String getHello() {
    return "{\"greet\":true}";
  }
}
