package com.yunkahui.datacubeper.home.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.IActivityStatusBar;

public class WithdrawSuccessActivity extends AppCompatActivity implements IActivityStatusBar {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_withdraw_success);
        super.onCreate(savedInstanceState);
        setTitle("提现成功");
    }

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
    }
}
