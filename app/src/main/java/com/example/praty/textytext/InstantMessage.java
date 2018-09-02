package com.example.praty.textytext;

public class InstantMessage {

    private String mAuthor;
    private String mMessage;

    public InstantMessage(String mAuthor, String mMessage) {
        this.mAuthor = mAuthor;
        this.mMessage = mMessage;
    }

    public InstantMessage() {
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmMessage() {
        return mMessage;
    }
}
