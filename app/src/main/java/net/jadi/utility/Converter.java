/*
 * Copyright (c) 2016. Behrouz Khezry
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

package net.jadi.utility;

import android.content.res.Resources;
import android.util.TypedValue;

import net.jadi.dao.PostBookmark;
import net.jadi.pojo.PostBlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Converter {
    public static int dpToPx(int dp, Resources resources) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }

    public static String toString(List<String> tagList) {
        String tags = "";
        for (String s : tagList) {
            if (tags.equals("")) {
                tags = s;
            } else {
                tags += "," + s;
            }
        }
        return tags;

    }

    public static List<String> toList(String tags) {
        List<String> tagList = new ArrayList<>();
        if (!tags.equals("")) {
            String[] splits = tags.split(",");
            Collections.addAll(tagList, splits);
        }
        return tagList;
    }

    public  PostBookmark getPostBookmark(PostBlog postBlog) {
        PostBookmark postBookmark = new PostBookmark();
        postBookmark.setId((postBlog.getId()));
        postBookmark.setDate(postBlog.getDate());
        postBookmark.setGuid(postBlog.getGuid());
        postBookmark.setModified(postBlog.getModified());
        postBookmark.setStatus(postBlog.getStatus());
        postBookmark.setType(postBlog.getType());
        postBookmark.setLink(postBlog.getLink());
        postBookmark.setTitle(postBlog.getTitle());
        postBookmark.setDescription(postBlog.getDescription());
        postBookmark.setAuthor(postBlog.getAuthor());
        postBookmark.setAuthorName(postBlog.getAuthorName());
        postBookmark.setFeaturedMedia(postBlog.getFeaturedMedia());
        postBookmark.setMediaDetails(postBlog.getMediaDetails());
        postBookmark.setCommentStatus(postBlog.getCommentStatus());
        postBookmark.setParent(postBlog.getParent());
        postBookmark.setCategories(Converter.toString(postBlog.getCategories()));
        postBookmark.setTags(Converter.toString(postBlog.getTags()));
        Long tsLong = System.currentTimeMillis();
        postBookmark.setCreateTime(tsLong);
        return postBookmark;
    }

    public  PostBlog getPostBlog(PostBookmark postBookmark) {
        PostBlog postBlog = new PostBlog();
        postBlog.setId((postBookmark.getId()));
        postBlog.setDate(postBookmark.getDate());
        postBlog.setGuid(postBookmark.getGuid());
        postBlog.setModified(postBookmark.getModified());
        postBlog.setStatus(postBookmark.getStatus());
        postBlog.setType(postBookmark.getType());
        postBlog.setLink(postBookmark.getLink());
        postBlog.setTitle(postBookmark.getTitle());
        postBlog.setDescription(postBookmark.getDescription());
        postBlog.setAuthor(postBookmark.getAuthor());
        postBlog.setAuthorName(postBookmark.getAuthorName());
        postBlog.setFeaturedMedia(postBookmark.getFeaturedMedia());
        postBlog.setMediaDetails(postBookmark.getMediaDetails());
        postBlog.setCommentStatus(postBookmark.getCommentStatus());
        postBlog.setParent(postBookmark.getParent());
        postBlog.setCategories(Converter.toList(postBookmark.getCategories()));
        postBlog.setTags(Converter.toList(postBookmark.getTags()));
        return postBlog;
    }

    public List<PostBlog> getPostBlogList(List<PostBookmark> postBookmarks) {
        List<PostBlog> postBlogs = new ArrayList<>();
        for (PostBookmark postBookmark : postBookmarks) {
            postBlogs.add(getPostBlog(postBookmark));
        }
        return postBlogs;
    }
}
