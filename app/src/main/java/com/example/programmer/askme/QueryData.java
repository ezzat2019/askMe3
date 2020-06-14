package com.example.programmer.askme;

public class QueryData {
    private String category,date,question_type,subject,subject_detiel;
    private boolean reply;

    public QueryData() {
    }

    public QueryData(String category, String date, String question_type, String subject, String subject_detiel, boolean reply) {
        this.category = category;
        this.date = date;
        this.question_type = question_type;
        this.subject = subject;
        this.subject_detiel = subject_detiel;
        this.reply = reply;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSubject_detiel(String subject_detiel) {
        this.subject_detiel = subject_detiel;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }
    public String  showQuery() {
        String str="\nQuery Type : "+getQuestion_type()+"\nCategory : "+getCategory()+"\nSubject : "+getSubject()+"\nDetiels :\n"+getSubject_detiel()+"\n";
        return str;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public String getSubject() {
        return subject;
    }

    public String getSubject_detiel() {
        return subject_detiel;
    }

    public boolean isReply() {
        return reply;
    }
}
