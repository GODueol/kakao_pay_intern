package goduoel.com.kakaointern.data.entity;

public enum ImageRequestType {
    ACCURACY("accuracy"), RECENCY("recency");

    private String type;

    private ImageRequestType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
