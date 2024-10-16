package com.fiap.techchallenge.utils;
import com.google.gson.Gson;

public abstract class ParseUtils {

  public static String toJson(Object obj) {
    Gson gson = new Gson();
    return gson.toJson(obj);
  }

}
