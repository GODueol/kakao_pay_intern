package goduoel.com.kakaointern.data.entity;

public class RequestHeader {

    private String query;
    private ImageRequestType sort;
    private int page;

    public RequestHeader(String query, ImageRequestType sort, int page) {
        this.query = query;
        this.sort = sort;
        this.page = page;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ImageRequestType getSort() {
        return sort;
    }

    public void setSort(ImageRequestType sort) {
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
