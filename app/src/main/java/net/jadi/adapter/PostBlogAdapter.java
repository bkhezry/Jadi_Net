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

package net.jadi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.bkhezry.extrawebview.ExtraWebViewCreator;
import com.github.bkhezry.extrawebview.data.DataModel;
import com.github.bkhezry.extrawebview.data.DataModelBuilder;
import com.github.bkhezry.extrawebview.data.ThemePreference;

import net.jadi.R;
import net.jadi.activity.TagSearchActivity;
import net.jadi.dao.DataBaseHandler;
import net.jadi.pojo.PostBlog;
import net.jadi.utility.Converter;
import net.jadi.utility.DateConverter;

import java.util.Calendar;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;


public class PostBlogAdapter extends RecyclerView.Adapter<PostBlogAdapter.MyViewHolder> {
    private Context mContext;
    private List<PostBlog> postBlogs;
    private DateConverter dateConverter = new DateConverter();
    private Typeface typeface;

    public PostBlogAdapter(Context mContext, List<PostBlog> postBlogPOJOs) {
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/IRANSansMobile.ttf");
        this.mContext = mContext;
        this.postBlogs = postBlogPOJOs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PostBlog postBlog = postBlogs.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(postBlog.getDate() + "000"));
        dateConverter.setGregorianDate(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        holder.date.setText(dateConverter.getIranianDate());
        holder.title.setText(postBlog.getTitle());
        holder.description.setText(String.format("%s...", postBlog.getDescription()));
        holder.showPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWebView(postBlog);
            }
        });
        holder.tagContainerLayout.setTags(postBlog.getTags());

    }

    private void startTagSearchActivity(String text) {
        Intent intent = new Intent(mContext, TagSearchActivity.class);
        intent.putExtra("tagName", text);
        mContext.startActivity(intent);
    }

    private void showWebView(PostBlog postBlog) {
        DataBaseHandler dataBaseHandler = new DataBaseHandler(mContext);
        boolean isBookMark = dataBaseHandler.isPostBookmark(postBlog.getId());
        DataModel dataModel = new DataModelBuilder()
                .withId(postBlog.getId())
                .withType("blog")
                .withBy(postBlog.getAuthorName())
                .withTime(postBlog.getDate())
                .withUrl(Converter.urlHTTPS(postBlog.getGuid()))
                .withDescription(postBlog.getDescription())
                .withBookmark(isBookMark)
                .withViewed(false)
                .withRank(0)
                .withVoted(false)
                .withPageTitle(postBlog.getTitle())
                .build();
        new ExtraWebViewCreator()
                .withContext(mContext)
                .withBookmarkIcon(true)
                .withVoteIcon(false)
                .withThemeName(ThemePreference.THEME_NAMES.get(0))
                .withCustomFont("fonts/IRANSansMobile.ttf")
                .withDataModel(dataModel)
                .show();
    }

    @Override
    public int getItemCount() {
        return postBlogs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, description;
        TagContainerLayout tagContainerLayout;
        RelativeLayout showPostLayout;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            title.setTypeface(title.getTypeface(), Typeface.BOLD);
            date = view.findViewById(R.id.date);
            description = view.findViewById(R.id.description);
            tagContainerLayout = view.findViewById(R.id.tagcontainerLayout);
            tagContainerLayout.setTagTypeface(typeface);
            tagContainerLayout.setIsTagViewClickable(true);
            tagContainerLayout.setGravity(Gravity.RIGHT);
            tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                @Override
                public void onTagClick(int position, String text) {
                    startTagSearchActivity(text);
                }

                @Override
                public void onTagLongClick(int position, String text) {
                }

                @Override
                public void onTagCrossClick(int position) {

                }
            });
            showPostLayout = view.findViewById(R.id.showPostLayout);
        }
    }
}
