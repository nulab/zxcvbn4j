package com.nulabinc.zxcvbn.matchers;

import java.io.InputStream;

class ResourceLoader {

    InputStream getInputStream(String path) {
        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (resource != null) {
            return resource;
        }
        return ClassLoader.getSystemResourceAsStream(path);
    }
}
