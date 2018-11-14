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
package net.jadi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.collection.LongSparseArray;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.bkhezry.extrawebview.data.IntentServiceResult;

import net.jadi.R;
import net.jadi.adapter.PostBlogAdapter;
import net.jadi.dao.DataBaseHandler;
import net.jadi.listener.EndlessRecyclerViewScrollListener;
import net.jadi.pojo.PostBlog;
import net.jadi.services.APIServices;
import net.jadi.services.RetrofitUtility;
import net.jadi.utility.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static net.jadi.activity.MainActivity.PER_PAGE;


public class TagSearchActivity extends AppCompatActivity {
    private int pageGlobal;
    private List<PostBlog> postBlogList;
    private List<PostBlog> postBlogListAll;
    private RecyclerView recyclerView;
    private PostBlogAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SmoothProgressBar smoothProgressBar;
    private String tagName;
    private LongSparseArray<PostBlog> postBlogSparseArray = new LongSparseArray<>();
    private DataBaseHandler dataBaseHandler;
    private MaterialDialog errorConnectionDialog;
    private MaterialDialog errorFilterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @StyleRes int style = R.style.AppTheme;
        setTheme(style);
        setContentView(R.layout.activity_tag_search);
        dialogGenerator();
        EventBus.getDefault().register(this);
        dataBaseHandler = new DataBaseHandler(TagSearchActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_HOME_AS_UP);
        Intent intent = getIntent();
        tagName = intent.getStringExtra("tagName");
        toolbar.setTitle(tagName);
        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.smooth_progressbar);
        initRecycleView();
    }

    private void dialogGenerator() {
        errorConnectionDialog = new MaterialDialog.Builder(TagSearchActivity.this)
                .content(R.string.error_connect_label)
                .contentGravity(GravityEnum.END)
                .cancelable(false)
                .autoDismiss(false)
                .typeface("IRANSansMobile.ttf", "IRANSansMobile.ttf")
                .positiveText(R.string.ok_label)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
        errorFilterDialog = new MaterialDialog.Builder(TagSearchActivity.this)
                .content(R.string.error_filter_label)
                .contentGravity(GravityEnum.END)
                .cancelable(false)
                .autoDismiss(false)
                .typeface("IRANSansMobile.ttf", "IRANSansMobile.ttf")
                .positiveText(R.string.shit_label)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
    }


    private void initRecycleView() {
        postBlogListAll = new ArrayList<>();
        postBlogList = new ArrayList<>();
        pageGlobal = 0;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new PostBlogAdapter(TagSearchActivity.this, postBlogList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TagSearchActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                preparePostBlog(++pageGlobal);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        preparePostBlog(++pageGlobal);

    }

    private void preparePostBlog(int page) {
        if (Utility.hasConnection(this)) {
            smoothProgressBar.setVisibility(View.VISIBLE);
            APIServices apiServices = RetrofitUtility.getRetrofit().create(APIServices.class);
            Call<List<PostBlog>> call = apiServices.getPostByTagService(PER_PAGE, page, tagName);
            call.enqueue(new Callback<List<PostBlog>>() {
                @Override
                public void onResponse(Call<List<PostBlog>> call, Response<List<PostBlog>> response) {
                    smoothProgressBar.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        List<PostBlog> postBlogPOJOs = response.body();
                        addDataToView(postBlogPOJOs);
                    }
                }

                @Override
                public void onFailure(Call<List<PostBlog>> call, Throwable t) {
                    errorFilterDialog.show();
                    smoothProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            errorConnectionDialog.show();
        }
    }

    private void addDataToView(List<PostBlog> postBlogs) {
        postBlogListAll.addAll(postBlogs);
        postBlogList.clear();
        postBlogList.addAll(postBlogListAll);
        adapter.notifyItemRangeInserted(postBlogListAll.size() - 10, postBlogs.size());
        scrollListener.resetState();
        for (PostBlog postBlog : postBlogs) {
            postBlogSparseArray.put(postBlog.getId(), postBlog);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(IntentServiceResult intentServiceResult) {
        if (intentServiceResult.getTypeEvent().equals("BOOKMARK")) {
            bookmark(intentServiceResult);
        }
    }

    private void bookmark(IntentServiceResult result) {
        if (postBlogSparseArray.get(result.getId()) != null)
            if (result.isChecked()) {
                dataBaseHandler.insertPostBookmark(postBlogSparseArray.get(result.getId()));
            } else {
                dataBaseHandler.removePostBookmark(result.getId());
            }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
