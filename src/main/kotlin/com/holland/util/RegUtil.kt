package com.holland.util

import java.util.regex.Pattern

object RegUtil {

    public fun hostCheck(host: String) =
        Pattern.matches("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(localhost)$", host)

    public fun portCheck(port: String) =
        Pattern.matches("^\\d{1,5}$", port)

}
