package net.jadi.dao;

import android.content.Context;

import net.jadi.pojo.PostBlog;
import net.jadi.utility.Converter;

import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * Created by bkhezry on 12/26/2016.
 */

public class DataBaseHandler {
    private Database db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Converter converter = new Converter();

    public DataBaseHandler(Context context) {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "jadi-db",
                null);
        db = helper.getWritableDb();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public void insertPostBookmark(PostBlog postBlog) {
        PostBookmarkDao postBookmarkDao = daoSession.getPostBookmarkDao();
        PostBookmark postBookmark = converter.getPostBookmark(postBlog);
        postBookmarkDao.insert(postBookmark);
    }

    public void removePostBookmark(Long id) {
        PostBookmarkDao postBookmarkDao = daoSession.getPostBookmarkDao();
        postBookmarkDao.deleteByKey(id);
    }


    public boolean isPostBookmark(Long id) {
        PostBookmarkDao postBookmarkDao = daoSession.getPostBookmarkDao();
        PostBookmark postBookmark = postBookmarkDao.load(id);
        return postBookmark != null;
    }

    public List<PostBlog> getPostBookmarks() {
        PostBookmarkDao postBookmarkDao = daoSession.getPostBookmarkDao();
        List<PostBookmark> postBookmarks = postBookmarkDao.queryBuilder()
                .orderDesc(PostBookmarkDao.Properties.CreateTime)
                .list();
        return converter.getPostBlogList(postBookmarks);
    }
}
