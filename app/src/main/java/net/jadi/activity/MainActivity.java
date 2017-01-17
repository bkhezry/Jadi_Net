package net.jadi.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.bkhezry.extrawebview.data.IntentServiceResult;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import net.jadi.R;
import net.jadi.adapter.PostBlogAdapter;
import net.jadi.dao.DataBaseHandler;
import net.jadi.listener.EndlessRecyclerViewScrollListener;
import net.jadi.pojo.PostBlog;
import net.jadi.services.APIServices;
import net.jadi.services.RetrofitUtility;

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
    private String category = "";
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private int pageGlobal;
    private List<PostBlog> postBlogList;
    private List<PostBlog> postBlogListAll;
    private RecyclerView recyclerView;
    private PostBlogAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SmoothProgressBar smoothProgressBar;
    private BoomMenuButton bmb;
    private Typeface typeface;
    private DataBaseHandler dataBaseHandler;
    private LongSparseArray<PostBlog> postBlogSparseArray = new LongSparseArray<>();
    private LoadType loadType;
    private MenuItem searchItem;

    public enum LoadType {
        ONLINE, DATABASE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @StyleRes int style = R.style.AppTheme;
        setTheme(style);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        loadType = LoadType.ONLINE;
        dataBaseHandler = new DataBaseHandler(MainActivity.this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_3);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            bmb.addBuilder(getTextInsideCircleButtonBuilder(imageResources[i], tags[i]));
        }


        IProfile profile = new ProfileDrawerItem()
                .withName("کیبرد آزاد")
                .withIcon(FontAwesome.Icon.faw_github)
                .withEmail("در دفاع از آزادی کیبرد");
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        profile)
                .withSavedInstance(savedInstanceState)
                .withTextColor(Color.BLACK)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("پست‌ها").withIcon(FontAwesome.Icon.faw_th_list).withIdentifier(1),
                        new PrimaryDrawerItem().withName("نشان شده‌ها").withIcon(FontAwesome.Icon.faw_bookmark).withIdentifier(1)
                        //new PrimaryDrawerItem().withName("تنظیمات").withIcon(FontAwesome.Icon.faw_cog),
                        //new PrimaryDrawerItem().withName("راهنما").withIcon(FontAwesome.Icon.faw_question),
                        //new PrimaryDrawerItem().withName("درباره").withIcon(FontAwesome.Icon.faw_github)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem.getIdentifier() == 1) {
                        }

                        if (drawerItem instanceof Nameable) {
                            toolbar.setTitle(((Nameable) drawerItem).getName().getText(MainActivity.this));
                        }
                        display(position);
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withDrawerGravity(Gravity.END)
                .build();
        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.smooth_progressbar);
        initRecycleView();


    }

    private void display(int position) {
        switch (position) {
            case 1:
                loadType = LoadType.ONLINE;
                searchItem.setVisible(true);
                initRecycleView();
                break;
            case 2:
                loadType = LoadType.DATABASE;
                searchItem.setVisible(false);
                initRecycleView();
                break;
            default:
                Toast.makeText(this, "هنوز تکمیل نشدن ولی میشن به زودی", Toast.LENGTH_SHORT).show();
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new PostBlogAdapter(MainActivity.this, postBlogList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        if (loadType == LoadType.ONLINE) {
            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    preparePostBlog(++pageGlobal);
                }
            };
            recyclerView.addOnScrollListener(scrollListener);
            preparePostBlog(++pageGlobal);
        } else {
            preparePostBlogDB();
        }
    }

    private void preparePostBlogDB() {
        addDataToView(dataBaseHandler.getPostBookmarks());
    }

    private void preparePostBlog(int page) {
        smoothProgressBar.setVisibility(View.VISIBLE);
        APIServices apiServices = RetrofitUtility.getRetrofit().create(APIServices.class);
        Call<List<PostBlog>> call = apiServices.getPostPlogsService(PER_PAGE, page, search, category);
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
                smoothProgressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void addDataToView(List<PostBlog> postBlogs) {
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
        if (id == R.id.show_drawer) {
            result.openDrawer();
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
                        intent.putExtra("tagName", tags[index]);
                        startActivity(intent);
                    }
                })
                .normalText(text);
    }

    private static int[] imageResources = new int[]{
            R.drawable.bat,
            R.drawable.bear,
            R.drawable.bee,
            R.drawable.butterfly,
            R.drawable.cat,
            R.drawable.deer,
            R.drawable.dolphin,
            R.drawable.eagle,
            R.drawable.horse
    };
    private static String[] tags = new String[]{
            "گنو/لینوکس",
            "حقوق بشر",
            "برنامه نویسی",
            "رادیوگیک",
            "ویدئوکست",
            "سرکوب دیجیتال",
            "آموزش",
            "معرفی",
            "خبر"
    };


}
