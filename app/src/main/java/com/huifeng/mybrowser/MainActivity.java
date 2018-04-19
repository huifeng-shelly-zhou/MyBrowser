package com.huifeng.mybrowser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.huifeng.mybrowser.Models.FavoriteModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String WEB_URL = "WEB_URL";
    private AutoCompleteTextView urlTxt;
    private WebView browser_web_view;
    private MenuItem favorIcon;
    private ProgressBar indeterminateBar;

    Toolbar myToolbar;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<FavoriteModel> favoriteList = new ArrayList<>();
    private FragmentSetting fragmentSetting;

    private String host = "";
    private String home_url = "https://www.google.ca";
    private String my_url = "https://www.google.ca";

    private SharedPreferences sharedPref;
    private String favorListName = "favorite_list";
    private String preName = "SharedPre_my_browser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_url = home_url;
                browser_web_view.loadUrl(my_url);
            }
        });


        urlTxt = findViewById(R.id.browser_url_txt);
        browser_web_view = findViewById(R.id.browser_web_view);

        indeterminateBar = findViewById(R.id.indeterminateBar);
        indeterminateBar.setVisibility(View.INVISIBLE);

        sharedPref = this.getSharedPreferences(preName, Context.MODE_PRIVATE);

        adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, list);
        urlTxt.setThreshold(1);//will start working from first character
        urlTxt.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        urlTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                host = "";
                if (urlTxt != null) {
                    my_url = list.get(i);
                    browser_web_view.loadUrl(my_url);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });

        browser_web_view.getSettings().setJavaScriptEnabled(false);
        browser_web_view.getSettings().setAppCacheEnabled(true);
        browser_web_view.getSettings().setBuiltInZoomControls(true);
        browser_web_view.getSettings().setDisplayZoomControls(false);
        browser_web_view.getSettings().setUseWideViewPort(true);
        browser_web_view.getSettings().setLoadWithOverviewMode(true);
        browser_web_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                indeterminateBar.setVisibility(View.VISIBLE);
                Log.i("onPageStarted", url);
                if (!urlTxt.getText().toString().equalsIgnoreCase(url)) {
                    urlTxt.setText(url);
                    my_url = url;
                }
                setHost(url);

                // update favor icon
                FavoriteModel favoriteModel = new FavoriteModel();
                favoriteModel.setUrl(url);
                favoriteModel.setTitle(view.getTitle());
                updateFavorColor(favoriteModel);

                // update browser history
                list.add(0, url);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLoadResource(WebView view,
                                       String url) {

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                String refine = url.replace("http://", "");
                refine = refine.replace("https://", "");

                Log.i("url", url);
                Log.i("host", host);
                if (!host.isEmpty() && (refine.startsWith(host) || host.contains("google"))) {
                    return false;
                }
                return true;

            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {

                Log.i("url", request.getUrl().toString());
                Log.i("host", host);

                if (host.equals(request.getUrl().getHost()) || host.contains("google")) {
                    return false;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view,
                                       String url) {
                Log.i("onPageFinished", url);

                indeterminateBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                String msg = "";
                if (error != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        msg = (String) error.getDescription();
                    }
                }

                Log.e("onReceivedError", msg);
            }

        });
        browser_web_view.setWebChromeClient(new WebChromeClient());

        if(savedInstanceState != null && savedInstanceState.getString(WEB_URL) != null){
            my_url = savedInstanceState.getString(WEB_URL);
        }

        browser_web_view.loadUrl(my_url);
        browser_web_view.requestFocus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favour_bar, menu);

        int positionOfMenuItem = 0; // or whatever...
        favorIcon = menu.getItem(positionOfMenuItem);
        FavoriteModel favoriteModel = getCurrentWebPage();
        updateFavorColor(favoriteModel);

        MenuItem more = menu.findItem(R.id.action_more);
        if(more != null){
            //more.getIcon().setColorFilter(getResources().getColor(R.color.dark), PorterDuff.Mode.SRC_IN);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_more:
                // User chose the "Settings" item, show the app settings UI...
                openSettingFragment();
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                FavoriteModel favoriteModel = getCurrentWebPage();
                toggleFavor(favoriteModel);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        if (browser_web_view.canGoBack()) {
            browser_web_view.goBack();
            host = "";

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(WEB_URL, my_url);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateFavorColor(FavoriteModel favor) {
        if(favorIcon != null){
            favoriteList = getSavedFavoriteList();
            favorIcon.getIcon().setColorFilter(getResources().getColor(R.color.dark), PorterDuff.Mode.SRC_IN);
            for (int i = 0; i < favoriteList.size(); i++) {
                if (favor.getUrl().equalsIgnoreCase(favoriteList.get(i).getUrl())) {
                    Drawable icon = favorIcon.getIcon();
                    if (icon != null) {
                        favorIcon.getIcon().setColorFilter(getResources().getColor(R.color.pink), PorterDuff.Mode.SRC_IN);
                    }
                    break;
                }
            }
        }
    }

    private void setHost(String inputUrl) {
        String refine = inputUrl.replace("http://", "");
        refine = refine.replace("https://", "");
        String[] splits = refine.split("/");
        if (splits.length > 0 && (host.isEmpty() || host.contains("google"))) {
            host = splits[0];
        }
    }

    public void goUrl(View v) {
        host = "";
        if (urlTxt != null) {
            my_url = urlTxt.getText().toString();
            browser_web_view.loadUrl(my_url);

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    private void openSettingFragment() {
        FrameLayout setting_container = findViewById(R.id.setting_container);
        if(setting_container != null){

            if(fragmentSetting == null){
                fragmentSetting = FragmentSetting.newInstance(getSavedFavoriteList());
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.setting_container, fragmentSetting)
                        .commit();
            }
            else {
                closeSettingFragment(setting_container);
            }


        }
    }

    public void closeSettingFragment(View v){
        if(fragmentSetting != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .remove(fragmentSetting)
                    .commit();
            fragmentSetting = null;
        }

    }

    private void toggleFavor(FavoriteModel favoriteModel) {

        if (!favoriteModel.getUrl().isEmpty()) {
            favoriteList = getSavedFavoriteList();

            int index = -1;
            for (int i = 0; i < favoriteList.size(); i++) {
                if (favoriteModel.getUrl().equalsIgnoreCase(favoriteList.get(i).getUrl())) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                favoriteList.remove(index);
            } else {
                favoriteList.add(0, favoriteModel);
            }
            saveFavoriteList();
            updateFavorColor(favoriteModel);
            updateSettingFragment();
        }
    }

    private FavoriteModel getCurrentWebPage() {
        FavoriteModel favoriteModel = new FavoriteModel();

        if (browser_web_view != null) {
            String url = browser_web_view.getUrl().trim();
            String title = browser_web_view.getTitle().trim();
            if (!url.isEmpty()) {
                favoriteModel.setTitle(title);
                favoriteModel.setUrl(url);
            }
        }

        return favoriteModel;
    }

    private ArrayList<FavoriteModel> getSavedFavoriteList() {
        ArrayList<FavoriteModel> fL = new ArrayList<>();

        if (sharedPref != null) {
            Gson gson = new GsonBuilder().create();
            String string = sharedPref.getString(favorListName, "");
            fL = gson.fromJson(string, new TypeToken<ArrayList<FavoriteModel>>() {
            }.getType());
            if (fL == null) {
                fL = new ArrayList<>();
            }
        }

        return fL;
    }

    private void saveFavoriteList() {
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            Gson gson = new GsonBuilder().create();
            String string = gson.toJson(favoriteList);
            editor.putString(favorListName, string);
            editor.commit();
        }
    }

    public void favorItemClick(View v){
        host = "";
        FavoriteModel favoriteModel = (FavoriteModel) v.getTag();
        if(favoriteModel != null && urlTxt != null){
            urlTxt.setText(favoriteModel.getUrl());
            my_url = urlTxt.getText().toString();
            browser_web_view.loadUrl(my_url);
            closeSettingFragment(v);

        }
    }

    public void emptyClick(View v){}

    public void deleteFavorite(View v){
        FavoriteModel favoriteModel = (FavoriteModel) v.getTag();
        if(favoriteModel != null){
            toggleFavor(favoriteModel);
        }
    }

    private void updateSettingFragment(){
        if(fragmentSetting != null && fragmentSetting.adapter != null){
            fragmentSetting.adapter.updateFavorList(getSavedFavoriteList());
        }
    }

    public void clearUrlText(View v){
        if(urlTxt != null){
            urlTxt.setText("");
        }
    }
}

