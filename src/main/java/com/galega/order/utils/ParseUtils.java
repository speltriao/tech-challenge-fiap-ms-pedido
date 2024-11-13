package com.galega.order.utils;

import com.google.gson.Gson;

public abstract class ParseUtils {

  public static String toJson(Object obj) {
    Gson gson = new Gson();
    return gson.toJson(obj);
  }

}
