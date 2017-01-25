/*
 * Copyright (c) 2017. Behrouz Khezry
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jadi.dao;

import android.content.Context;

import net.jadi.pojo.PostBlog;
import net.jadi.utility.Converter;

import org.greenrobot.greendao.database.Database;

import java.util.List;


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
