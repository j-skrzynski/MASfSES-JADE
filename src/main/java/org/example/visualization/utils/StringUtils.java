package org.example.visualization.utils;

public class StringUtils {
    public static String hideString(String str, int lengthConstraint) {
        if (str.length() <= Math.max(lengthConstraint, 3)) {
            return str;
        }

        return str.substring(0, lengthConstraint - 3) + "...";
    }
}
