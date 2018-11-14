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

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.collection.LongSparseArray;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.bkhezry.extrawebview.data.IntentServiceResult;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.ss.bottomnavigation.BottomNavigation;
import com.ss.bottomnavigation.events.OnSelectedItemChangeListener;

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

public class MainActivity extends AppCompatActivity {
    public static final int PER_PAGE = 10;
    private String search = "";

    private int pageGlobal;
    private List<PostBlog> postBlogList;
    private List<PostBlog> postBlogListAll;
    private PostBlogAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SmoothProgressBar smoothProgressBar;
    private Typeface typeface;
    private DataBaseHandler dataBaseHandler;
    private LongSparseArray<PostBlog> postBlogSparseArray = new LongSparseArray<>();
    private LoadType loadType;
    private MenuItem searchItem;
    private MaterialDialog errorConnectionDialog;
    private MaterialDialog errorFilterDialog;
    Call<List<PostBlog>> call;

    public enum LoadType {
        ONLINE, DATABASE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @StyleRes int style = R.style.AppTheme;
        setTheme(style);
        setContentView(R.layout.activity_main);
        dialogGenerator();
        EventBus.getDefault().register(this);
        loadType = LoadType.ONLINE;
        dataBaseHandler = new DataBaseHandler(MainActivity.this);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");

        boomMenuGenerator();

        smoothProgressBar = findViewById(R.id.smooth_progressbar);
        BottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setTypeface(typeface);
        bottomNavigation.setOnSelectedItemChangeListener(new OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChanged(int itemId) {
                switch (itemId) {
                    case R.id.tab_posts:
                        display(1);
                        break;
                    case R.id.tab_bookmark:
                        display(2);
                        break;
                    case R.id.tab_feedback:
                        display(3);
                        break;

                }
            }
        });
        initRecycleView();
    }

    private void boomMenuGenerator() {
        BoomMenuButton bmb = findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_3);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            bmb.addBuilder(getTextInsideCircleButtonBuilder(Utility.ImageResources[i], Utility.TAGS[i]));
        }
    }

    private void dialogGenerator() {
        errorConnectionDialog = new MaterialDialog.Builder(MainActivity.this)
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
        errorFilterDialog = new MaterialDialog.Builder(MainActivity.this)
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

    private void display(int position) {
        switch (position) {
            case 1:
                loadType = LoadType.ONLINE;
                if (searchItem != null)
                    searchItem.setVisible(true);
                initRecycleView();
                break;
            case 2:
                smoothProgressBar.setVisibility(View.INVISIBLE);
                loadType = LoadType.DATABASE;
                searchItem.setVisible(false);
                initRecycleView();
                break;
            default:
                Toast.makeText(this, R.string.todo_label, Toast.LENGTH_SHORT).show();
                break;
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
                if (loadType == LoadType.DATABASE) {
                    int position = postBlogList.indexOf(postBlogSparseArray.get(result.getId()));
                    postBlogList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
    }

    private void initRecycleView() {
        postBlogListAll = new ArrayList<>();
        postBlogList = new ArrayList<>();
        pageGlobal = 0;
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new PostBlogAdapter(MainActivity.this, postBlogList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        if (loadType == LoadType.ONLINE) {
            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    preparePostBlogServer(++pageGlobal);
                }
            };
            recyclerView.addOnScrollListener(scrollListener);
            preparePostBlogServer(++pageGlobal);
        } else {
            preparePostBlogDB();
        }
    }

    private void preparePostBlogDB() {
        addDataToView(dataBaseHandler.getPostBookmarks(), LoadType.DATABASE);
    }

    private void preparePostBlogServer(int page) {
        if (Utility.hasConnection(this)) {
            smoothProgressBar.setVisibility(View.VISIBLE);
            APIServices apiServices = RetrofitUtility.getRetrofit().create(APIServices.class);
            String category = "";
            call = apiServices.getPostPlogsService(PER_PAGE, page, search, category);
            call.enqueue(new Callback<List<PostBlog>>() {
                @Override
                public void onResponse(Call<List<PostBlog>> call, Response<List<PostBlog>> response) {
                    smoothProgressBar.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        List<PostBlog> postBlogPOJOs = response.body();
                        if (loadType == LoadType.ONLINE)
                            addDataToView(postBlogPOJOs, LoadType.ONLINE);
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

    private void addDataToView(List<PostBlog> postBlogs, LoadType type) {
        postBlogListAll.addAll(postBlogs);
        postBlogList.clear();
        postBlogList.addAll(postBlogListAll);
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
        for (PostBlog postBlog : postBlogs) {
            postBlogSparseArray.put(postBlog.getId(), postBlog);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.search);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                search = query;
                initRecycleView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_github) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        } else if (id == R.id.refresh) {
            search = "";
            initRecycleView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    TextInsideCircleButton.Builder getTextInsideCircleButtonBuilder(int resource, final String text) {
        return new TextInsideCircleButton.Builder()
                .normalImageRes(resource)
                .typeface(typeface)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent = new Intent(MainActivity.this, TagSearchActivity.class);
                        intent.putExtra("tagName", Utility.TAGS[index]);
                        startActivity(intent);
                    }
                })
                .normalTextColor(Color.BLACK)
                .normalText(text);
    }
}
