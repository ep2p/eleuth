package com.github.ep2p.eleuth.util;

import java.io.File;

public class Path {
    public static String combine(String... paths) {
        File file = new File(paths[0]);

        for (int i = 1; i < paths.length ; i++) {
            file = new File(file, paths[i]);
        }

        return file.getPath();
    }
}