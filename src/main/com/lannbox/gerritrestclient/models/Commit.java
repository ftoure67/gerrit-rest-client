package com.lannbox.gerritrestclient.models;

import com.google.gson.annotations.Since;

import java.util.Date;

@Since(2.5)
@SuppressWarnings("unused")
public class Commit {
    public String commit;
    public Commit[] parents;
    public Person author;
    public Person committer;
    public String subject;
    public String message;

    public static class Person {
        public String name;
        public String email;
        public Date date;
        public int tz;
    }
}
