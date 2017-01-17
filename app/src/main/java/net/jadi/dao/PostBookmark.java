package net.jadi.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by bkhezry on 12/26/2016.
 */
@Entity(
        active = true
)
public class PostBookmark {
    @Id
    private Long id;
    private Long date;
    private String guid;
    private Long modified;
    private String status;
    private String type;
    private String link;
    private String title;
    private String description;
    private Integer author;
    private String authorName;
    private Integer featuredMedia;
    private boolean mediaDetails;
    private String commentStatus;
    private Integer parent;
    private String categories;
    private String tags;
    private Long createTime;
/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;
/** Used for active entity operations. */
@Generated(hash = 1351016272)
private transient PostBookmarkDao myDao;
@Generated(hash = 305925812)
public PostBookmark(Long id, Long date, String guid, Long modified,
        String status, String type, String link, String title,
        String description, Integer author, String authorName,
        Integer featuredMedia, boolean mediaDetails, String commentStatus,
        Integer parent, String categories, String tags, Long createTime) {
    this.id = id;
    this.date = date;
    this.guid = guid;
    this.modified = modified;
    this.status = status;
    this.type = type;
    this.link = link;
    this.title = title;
    this.description = description;
    this.author = author;
    this.authorName = authorName;
    this.featuredMedia = featuredMedia;
    this.mediaDetails = mediaDetails;
    this.commentStatus = commentStatus;
    this.parent = parent;
    this.categories = categories;
    this.tags = tags;
    this.createTime = createTime;
}
@Generated(hash = 1906520715)
public PostBookmark() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public Long getDate() {
    return this.date;
}
public void setDate(Long date) {
    this.date = date;
}
public String getGuid() {
    return this.guid;
}
public void setGuid(String guid) {
    this.guid = guid;
}
public Long getModified() {
    return this.modified;
}
public void setModified(Long modified) {
    this.modified = modified;
}
public String getStatus() {
    return this.status;
}
public void setStatus(String status) {
    this.status = status;
}
public String getType() {
    return this.type;
}
public void setType(String type) {
    this.type = type;
}
public String getLink() {
    return this.link;
}
public void setLink(String link) {
    this.link = link;
}
public String getTitle() {
    return this.title;
}
public void setTitle(String title) {
    this.title = title;
}
public String getDescription() {
    return this.description;
}
public void setDescription(String description) {
    this.description = description;
}
public Integer getAuthor() {
    return this.author;
}
public void setAuthor(Integer author) {
    this.author = author;
}
public String getAuthorName() {
    return this.authorName;
}
public void setAuthorName(String authorName) {
    this.authorName = authorName;
}
public Integer getFeaturedMedia() {
    return this.featuredMedia;
}
public void setFeaturedMedia(Integer featuredMedia) {
    this.featuredMedia = featuredMedia;
}
public boolean getMediaDetails() {
    return this.mediaDetails;
}
public void setMediaDetails(boolean mediaDetails) {
    this.mediaDetails = mediaDetails;
}
public String getCommentStatus() {
    return this.commentStatus;
}
public void setCommentStatus(String commentStatus) {
    this.commentStatus = commentStatus;
}
public Integer getParent() {
    return this.parent;
}
public void setParent(Integer parent) {
    this.parent = parent;
}
public String getCategories() {
    return this.categories;
}
public void setCategories(String categories) {
    this.categories = categories;
}
public String getTags() {
    return this.tags;
}
public void setTags(String tags) {
    this.tags = tags;
}
public Long getCreateTime() {
    return this.createTime;
}
public void setCreateTime(Long createTime) {
    this.createTime = createTime;
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 128553479)
public void delete() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.delete(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 1942392019)
public void refresh() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.refresh(this);
}
/**
 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
 * Entity must attached to an entity context.
 */
@Generated(hash = 713229351)
public void update() {
    if (myDao == null) {
        throw new DaoException("Entity is detached from DAO context");
    }
    myDao.update(this);
}
/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 2053290355)
public void __setDaoSession(DaoSession daoSession) {
    this.daoSession = daoSession;
    myDao = daoSession != null ? daoSession.getPostBookmarkDao() : null;
}
}
