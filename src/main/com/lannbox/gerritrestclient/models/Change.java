package com.lannbox.gerritrestclient.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Since(2.5)
@SuppressWarnings("unused")
public class Change {
    public String id;
    public String project;
    public String branch;
    public String topic;
    public String subject;
    public String status;
    public Date created;
    public Date updated;
    public boolean reviewed;
    @SerializedName("_number") public int number;
    public User owner;

    // Optional fields (depending on flags)
    public Map<String, Label> labels;

    @SerializedName("current_revision") public String current_revision_hash;
    public Map<String, Revision> revisions;

    // Query result fields
    private String _sortkey;
    private boolean _more_changes;

    public Revision getCurrentRevision() {
        if (current_revision_hash != null && revisions != null) {
            return revisions.get(current_revision_hash);
        }
        return null;
    }

    public static class Label {
        public User approved;
        public User recommended;
        public User disliked;
        public User rejected;
    }

    public static class ResultList extends ArrayList<Change> {
        public String getSortkey(int i) {
            return get(i)._sortkey;
        }

        public String prevPageKey() {
            if (!isEmpty()) {
                Change change = get(0);
                if (change._more_changes) {
                    return change._sortkey;
                }
            }
            return null;
        }

        public String nextPageKey() {
            if (!isEmpty()) {
                Change change = get(size() - 1);
                if (change._more_changes) {
                    return change._sortkey;
                }
            }
            return null;
        }
    }
}
