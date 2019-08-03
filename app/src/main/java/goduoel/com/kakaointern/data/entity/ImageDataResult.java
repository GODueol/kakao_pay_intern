package goduoel.com.kakaointern.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class ImageDataResult {

    @SerializedName("meta")
    @Expose
    private ImageMeta meta;
    @SerializedName("documents")
    @Expose
    private List<ImageDocument> documents = null;

    public ImageMeta getMeta() {
        return meta;
    }

    public void setMeta(ImageMeta meta) {
        this.meta = meta;
    }

    public List<ImageDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ImageDocument> documents) {
        this.documents = documents;
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
        @Expose
        private Integer totalCount;
        @SerializedName("pageable_count")
        @Expose
        private Integer pageableCount;
        @SerializedName("is_end")
        @Expose
        private Boolean isEnd;

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getPageableCount() {
            return pageableCount;
        }

        public void setPageableCount(Integer pageableCount) {
            this.pageableCount = pageableCount;
        }

        public Boolean getIsEnd() {
            return isEnd;
        }

        public void setIsEnd(Boolean isEnd) {
            this.isEnd = isEnd;
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

    public static class ImageDocument implements Parcelable {

        @SerializedName("collection")
        @Expose
        private String collection;
        @SerializedName("thumbnail_url")
        @Expose
        private String thumbnailUrl;
        @SerializedName("image_url")
        @Expose
        private String imageUrl;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("display_sitename")
        @Expose
        private String displaySitename;
        @SerializedName("doc_url")
        @Expose
        private String docUrl;
        @SerializedName("datetime")
        @Expose
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

        public static final Creator<ImageDocument> CREATOR = new Creator<ImageDocument>() {
            @Override
            public ImageDocument createFromParcel(Parcel in) {
                return new ImageDocument(in);
            }

            @Override
            public ImageDocument[] newArray(int size) {
                return new ImageDocument[size];
            }
        };

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getDisplaySitename() {
            return displaySitename;
        }

        public void setDisplaySitename(String displaySitename) {
            this.displaySitename = displaySitename;
        }

        public String getDocUrl() {
            return docUrl;
        }

        public void setDocUrl(String docUrl) {
            this.docUrl = docUrl;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(collection);
            dest.writeString(thumbnailUrl);
            dest.writeString(imageUrl);
            if (width == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(width);
            }
            if (height == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(height);
            }
            dest.writeString(displaySitename);
            dest.writeString(docUrl);
            dest.writeString(datetime);
        }
    }
}