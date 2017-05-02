package com.together.ws.dto;

/**
 * Created by admin on 5/2/2017.
 */
public class Content {

    private byte[] body;

    private String type;

    public Content() {}

    public byte[] getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setType(String type) {
        this.type = type;
    }
}
