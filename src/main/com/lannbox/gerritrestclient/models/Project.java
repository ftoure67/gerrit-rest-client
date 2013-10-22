package com.lannbox.gerritrestclient.models;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Since;

import java.util.Map;

@Since(2.5)
@SuppressWarnings("unused")
public class Project {
    public String name;
    public String parent;
    public String description;
    public Map<String, String> branches;

    public static GsonBuilder registerGsonAdapter(GsonBuilder builder, double version) {
        try {
            return builder.registerTypeAdapter(Project[].class, new IndexedMapDeserializer<Project>("name", Project.class));
        } catch (NoSuchFieldException e) {
            return null; // Only if Project.name disappeared
        }
    }
}
