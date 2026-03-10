package com.poc;

public record File(String name, String content, boolean isEncrypted) {
    File(String name, String content) {
        this(name, content, false);
    }
}
