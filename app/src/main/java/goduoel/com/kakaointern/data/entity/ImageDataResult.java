package goduoel.com.kakaointern.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class ImageDataResult {

    @SerializedName("meta")
    private ImageMeta meta;
    @SerializedName("documents")
    private List<ImageDocument> documents = null;

    public ImageDataResult() {
    }

    public ImageMeta getMeta() {
        return meta;
    }

    public List<ImageDocument> getDocuments() {
        return documents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDataResult that = (ImageDataResult) o;
        return Objects.equals(meta, that.meta) &&
                Objects.equals(documents, that.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meta, documents);
    }

    @Override
    public String toString() {
        return "ImageResult{" +
                "meta=" + meta +
                ", documents=" + documents +
                '}';
    }

    public static class ImageMeta {

        @SerializedName("total_count")
        private Integer totalCount;
        @SerializedName("pageable_count")
        private Integer pageableCount;
        @SerializedName("is_end")
        private Boolean isEnd;

        public Integer getTotalCount() {
            return totalCount;
        }

        public Integer getPageableCount() {
            return pageableCount;
        }

        public Boolean getIsEnd() {
            return isEnd;
        }

        @Override
        public String toString() {
            return "Meta{" +
                    "totalCount=" + totalCount +
                    ", pageableCount=" + pageableCount +
                    ", isEnd=" + isEnd +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageMeta meta = (ImageMeta) o;
            return Objects.equals(totalCount, meta.totalCount) &&
                    Objects.equals(pageableCount, meta.pageableCount) &&
                    Objects.equals(isEnd, meta.isEnd);
        }

        @Override
        public int hashCode() {
            return Objects.hash(totalCount, pageableCount, isEnd);
        }

    }

    public static class ImageDocument {

        @SerializedName("collection")
        private String collection;
        @SerializedName("thumbnail_url")
        private String thumbnailUrl;
        @SerializedName("image_url")
        private String imageUrl;
        @SerializedName("width")
        private Integer width;
        @SerializedName("height")
        private Integer height;
        @SerializedName("display_sitename")
        private String displaySitename;
        @SerializedName("doc_url")
        private String docUrl;
        @SerializedName("datetime")
        private String datetime;

        protected ImageDocument(Parcel in) {
            collection = in.readString();
            thumbnailUrl = in.readString();
            imageUrl = in.readString();
            if (in.readByte() == 0) {
                width = null;
            } else {
                width = in.readInt();
            }
            if (in.readByte() == 0) {
                height = null;
            } else {
                height = in.readInt();
            }
            displaySitename = in.readString();
            docUrl = in.readString();
            datetime = in.readString();
        }

        public String getCollection() {
            return collection;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Integer getWidth() {
            return width;
        }

        public Integer getHeight() {
            return height;
        }

        public String getDisplaySitename() {
            return displaySitename;
        }

        public String getDocUrl() {
            return docUrl;
        }

        public String getDatetime() {
            return datetime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageDocument document = (ImageDocument) o;
            return Objects.equals(collection, document.collection) &&
                    Objects.equals(thumbnailUrl, document.thumbnailUrl) &&
                    Objects.equals(imageUrl, document.imageUrl) &&
                    Objects.equals(width, document.width) &&
                    Objects.equals(height, document.height) &&
                    Objects.equals(displaySitename, document.displaySitename) &&
                    Objects.equals(docUrl, document.docUrl) &&
                    Objects.equals(datetime, document.datetime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(collection, thumbnailUrl, imageUrl, width, height, displaySitename, docUrl, datetime);
        }

        @Override
        public String toString() {
            return "Document{" +
                    "collection='" + collection + '\'' +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", displaySitename='" + displaySitename + '\'' +
                    ", docUrl='" + docUrl + '\'' +
                    ", datetime='" + datetime + '\'' +
                    '}';
        }

    }
}