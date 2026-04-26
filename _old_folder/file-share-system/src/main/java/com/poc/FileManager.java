package com.poc;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author diegoUmpierre
 * @since Mar 09 2026
 */
public class FileManager {

    private final Map<String, File> files = new HashMap<>();


    public void save(File file) {
        files.put(file.name(), file);
        System.out.println("File " + file.name() + " has been saved.");
    }

    public void delete(File file) {
        files.remove(file.name());
        System.out.println("File " + file.name() + " has been deleted.");
    }

    public void listAll() {
        if (files.isEmpty()) {
            System.out.println("No files have been saved.");
        } else {
            files.forEach((fileName, file) -> {
                System.out.println("File " + fileName + ":");
            });
        }
    }

    public void restore(File file) {
        if (files.containsKey(file.name())) {
            System.out.println("File '" + file.name() + "' restored.");
        } else {
            System.out.println("File '" + file.name() + "' does not exist.");
        }
    }


    public void search(String query) {
        var results = files.keySet().stream()
                .filter(name -> name.contains(query))
                .toList();
        if (results.isEmpty()) {
            System.out.println("No files have been find.");
        } else {
            files.forEach((fileName, file) -> {
                System.out.println("File " + fileName + ":");
            });
        }
    }







}
