package com.yunkahui.datacubeper.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.hellokiki.rrorequest.HttpManager;
import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.common.api.BaseUrl;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018/3/22.
 */

public class CardDoctorApplication extends Application {

    private final int DESIGN_WIDTH=375;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context=this;
        HttpManager.baseUrl(BaseUrl.HOME);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                resetDensity(context,DESIGN_WIDTH);
                resetDensity(activity,DESIGN_WIDTH);
                setImmersiveStatusBar(activity);
                if(activity instanceof IActivityBase){
                    ((IActivityBase)activity).initData();
                    ((IActivityBase)activity).initView();
                }

            }

            @Override
            public void onActivityStarted(Activity activity) {
                resetDensity(context,DESIGN_WIDTH);
                resetDensity(activity,DESIGN_WIDTH);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                setToolBar(activity);
                resetDensity(context,DESIGN_WIDTH);
                resetDensity(activity,DESIGN_WIDTH);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }

    /**
     * 设置ToolBar
     * @param activity
     */
    private void setToolBar(final Activity activity){
        if(activity.findViewById(R.id.tool_bar)!=null){
            Toolbar toolbar=activity.findViewById(R.id.tool_bar);
            ((AppCompatActivity)activity).setSupportActionBar(toolbar);
            ActionBar actionBar= ((AppCompatActivity)activity).getSupportActionBar();
            if(actionBar!=null){
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
    }

    /**
     * 设置状态栏
     * @param activity
     */
    private void setImmersiveStatusBar(Activity activity){
        if(activity instanceof IActivityStatusBar){
            setTranslucentStatus(activity);
            addImmersiveStatusBar(activity,((IActivityStatusBar)activity).getStatusBarColor());
        }
    }

    /**
     * 添加自定义状态栏
     * @param activity
     */
    private void addImmersiveStatusBar(Activity activity,int color){
        ViewGroup contentFrameLayout = activity.findViewById(android.R.id.content);
        View contentView = contentFrameLayout.getChildAt(0);
        if (contentView != null && Build.VERSION.SDK_INT >= 14) {
            contentView.setFitsSystemWindows(true);
        }

        View statusBar=new View(activity);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = getStatusBarHeight();
        statusBar.setLayoutParams(params);
        statusBar.setBackgroundColor(color);
        contentFrameLayout.addView(statusBar);
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 设置状态栏为透明
     * @param activity
     */
    private void setTranslucentStatus(Activity activity) {
        //******** 5.0以上系统状态栏透明 ********
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window =activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     *   以pt为单位重新计算大小
     */
    public static void resetDensity(Context context, float designWidth){
        if(context == null)
            return;
        Point size = new Point();
        ((WindowManager)context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        Resources resources = context.getResources();
        resources.getDisplayMetrics().xdpi = size.x/designWidth*72f;
        DisplayMetrics metrics = getMetricsOnMIUI(context.getResources());
        if(metrics != null)
            metrics.xdpi = size.x/designWidth*72f;
    }

    /**
     *  解决MIUI屏幕适配问题
     * @param resources
     * @return
     */
    private static DisplayMetrics getMetricsOnMIUI(Resources resources){
        if("MiuiResources".equals(resources.getClass().getSimpleName()) || "XResources".equals(resources.getClass().getSimpleName())){
            try {
                Field field = Resources.class.getDeclaredField("mTmpMetrics");
                field.setAccessible(true);
                return  (DisplayMetrics) field.get(resources);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


}
