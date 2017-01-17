package net.jadi.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bkhezry on 12/24/2016.
 */

public class PostBlog {
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("date")
    @Expose
    private Long date;
    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("modified")
    @Expose
    private Long modified;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("author")
    @Expose
    private Integer author;
    @SerializedName("author_name")
    @Expose
    private String authorName;
    @SerializedName("featured_media")
    @Expose
    private Integer featuredMedia;
    @SerializedName("media_details")
    @Expose
    private Boolean mediaDetails;
    @SerializedName("parent")
    @Expose
    private Integer parent;
    @SerializedName("comment_status")
    @Expose
    private String commentStatus;
    @SerializedName("categories")
    @Expose
    private List<String> categories = null;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getFeaturedMedia() {
        return featuredMedia;
    }

    public void setFeaturedMedia(Integer featuredMedia) {
        this.featuredMedia = featuredMedia;
    }

    public Boolean getMediaDetails() {
        return mediaDetails;
    }

    public void setMediaDetails(Boolean mediaDetails) {
        this.mediaDetails = mediaDetails;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
