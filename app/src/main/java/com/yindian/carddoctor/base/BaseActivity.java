package com.yindian.carddoctor.base;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yindian.carddoctor.R;

import org.w3c.dom.Attr;

import butterknife.ButterKnife;

/**
 * 统一Activity，所有activity继承该activity
 */
public abstract class BaseActivity extends AppCompatActivity {

    private View viewStatusBarPlace;
    private FrameLayout frameLayoutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetDensity();
        super.setContentView(R.layout.activity_compat_status_bar);
        viewStatusBarPlace = findViewById(R.id.view_status_bar_place);
        frameLayoutContent = findViewById(R.id.frame_layout_content_place);
        ViewGroup.LayoutParams params = viewStatusBarPlace.getLayoutParams();
        params.height = getStatusBarHeight();
        viewStatusBarPlace.setLayoutParams(params);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initialize();
    }

    protected abstract int getLayoutId();

    public abstract void initialize();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        frameLayoutContent.addView(contentView);
    }

    //******** 获取状态栏高度 ********
    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    //******** 设置沉浸式状态栏 ********
    protected void setImmersiveStatusBar(int statusBarColor) {
        setTranslucentStatus();
        setStatusBarPlaceColor(statusBarColor);
    }

    private void setStatusBarPlaceColor(int statusColor) {
        if (viewStatusBarPlace != null) {
            viewStatusBarPlace.setBackgroundColor(statusColor);
        }
    }

    //******** 设置状态栏透明 ********
    private void setTranslucentStatus() {
        //******** 5.0以上系统状态栏透明 ********
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetDensity();
    }

    public final static float DESIGN_WIDTH = 375;

    public void resetDensity() {
        Point size = new Point();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        getResources().getDisplayMetrics().xdpi = size.x / DESIGN_WIDTH * 72f;
    }

}
