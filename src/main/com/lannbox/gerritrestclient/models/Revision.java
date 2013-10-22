package com.lannbox.gerritrestclient.models;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

@Since(2.5)
@SuppressWarnings("unused")
public class Revision {
    public String commit_hash;
    public boolean draft;
    @SerializedName("_number") public int number;
    public Map<String, FetchInfo> fetch;
    public Commit commit;
    public Map<String, FileInfo> files;

    public static GsonBuilder registerGsonAdapter(GsonBuilder builder, double version) {
        try {
            return builder.registerTypeAdapter(
                    new TypeToken<Map<String, Revision>>(){}.getType(),
                    new IndexedMapDeserializer<Revision>("commit_hash", Revision.class) {
                        @Override
                        public Map<String, Revision> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            Map<String, Revision> map = super.deserialize(json, typeOfT, context);
                            for (Map.Entry<String, Revision> entry : map.entrySet()) {
                                Commit commit = entry.getValue().commit;
                                if (commit != null && commit.commit == null) {
                                    commit.commit = entry.getKey();
                                }
                            }
                            return map;
                        }
                    });
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e); // Only if Revision.commit_hash disappeared
        }
    }

    public static class FetchInfo {
        public String url;
        public String ref;
    }

    public static class FileInfo {
        public String status;
        public boolean binary;
        public String old_path;
        public int lines_inserted;
        public int lines_deleted;
    }
}
