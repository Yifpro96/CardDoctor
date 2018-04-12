package com.yunkahui.datacubeper.upgradeJoin.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.JsonObject;
import com.hellokiki.rrorequest.SimpleCallBack;
import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.IActivityStatusBar;
import com.yunkahui.datacubeper.common.bean.BaseBeanList;
import com.yunkahui.datacubeper.common.bean.VipPackage;
import com.yunkahui.datacubeper.common.utils.LogUtils;
import com.yunkahui.datacubeper.common.utils.RequestUtils;
import com.yunkahui.datacubeper.common.utils.ToastUtils;
import com.yunkahui.datacubeper.common.view.LoadingViewDialog;
import com.yunkahui.datacubeper.upgradeJoin.logic.UpgradeVipLogic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * VIP会员套餐
 */
public class UpgradeVipActivity extends AppCompatActivity implements IActivityStatusBar,View.OnClickListener, UpgradeJoinItemView.OnChildClickListener {

    private final int RESULT_CODE_VIP1=1001;
    private final int RESULT_CODE_VIP2=1002;
    private final int RESULT_CODE_VIP3=1003;

    private UpgradeJoinItemView mUpgradeJoinItemView1;
    private UpgradeJoinItemView mUpgradeJoinItemView2;
    private UpgradeJoinItemView mUpgradeJoinItemView3;

    private List<VipPackage> mVipPackages=new ArrayList<>();
    private UpgradeVipLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_upgrade_vip);
        super.onCreate(savedInstanceState);
        setTitle("VIP会员套餐");
        mUpgradeJoinItemView1=findViewById(R.id.upgrade_join_item_view1);
        mUpgradeJoinItemView2=findViewById(R.id.upgrade_join_item_view2);
        mUpgradeJoinItemView3=findViewById(R.id.upgrade_join_item_view3);

        mUpgradeJoinItemView1.setOnClickListener(this);
        mUpgradeJoinItemView2.setOnClickListener(this);
        mUpgradeJoinItemView3.setOnClickListener(this);
        mUpgradeJoinItemView1.setOnChildClickListener(this);
        mUpgradeJoinItemView2.setOnChildClickListener(this);
        mUpgradeJoinItemView3.setOnChildClickListener(this);

        mLogic=new UpgradeVipLogic();

        initActionBar();
        loadData();
    }

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.setTitle("VIP会员套餐");
    }


    private void loadData(){
        LoadingViewDialog.getInstance().show(this);
        mLogic.loadData(this, new SimpleCallBack<BaseBeanList<VipPackage>>() {
            @Override
            public void onSuccess(BaseBeanList<VipPackage> vipPackageBaseBeanList) {
                LoadingViewDialog.getInstance().dismiss();
                LogUtils.e("Vip会员套餐->"+vipPackageBaseBeanList.toString());
                try {

                    if(RequestUtils.SUCCESS.equals(vipPackageBaseBeanList.getRespCode())){
                        mVipPackages.clear();
                        mVipPackages.addAll(vipPackageBaseBeanList.getRespData());
                        mUpgradeJoinItemView1.setData(mVipPackages.get(0));
                        mUpgradeJoinItemView2.setData(mVipPackages.get(1));
                        mUpgradeJoinItemView3.setData(mVipPackages.get(2));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                LoadingViewDialog.getInstance().dismiss();
            }
        });
    }


    private void loadVipData(int type){
        switch (type){
            case RESULT_CODE_VIP1:

                break;
            case RESULT_CODE_VIP2:
                showPayDialog("开通套餐二支付\n¥"+mVipPackages.get(1).getPrice(),RESULT_CODE_VIP2);
                break;
            case RESULT_CODE_VIP3:
                showPayDialog("开通套餐三支付\n¥"+mVipPackages.get(2).getPrice(),RESULT_CODE_VIP3);
                break;
        }
    }


    private void showDialog(String text,boolean isCancel){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this)
                .setMessage(text);
        if(!isCancel){
            dialog.setPositiveButton("关闭",null);
        }else{
            dialog.setPositiveButton("前往", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    PosPayActivity.actionStart(UpgradeVipActivity.this);
                }
            }).setNeutralButton("取消",null);
        }
        dialog.create().show();
    }


    private void showPayDialog(String text, final int type){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this)
                .setMessage(text);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(mVipPackages.size()<=0){
                        return;
                    }
                    switch (type){
                        case RESULT_CODE_VIP2:
                            payVip(mVipPackages.get(1).getVpId()+"");
                            break;
                        case RESULT_CODE_VIP3:
                            payVip(mVipPackages.get(2).getVpId()+"");
                            break;
                    }

                }
            }).setNeutralButton("取消",null);
        dialog.create().show();
    }


    private void payVip(String vipId){
        LoadingViewDialog.getInstance().show(this);
        mLogic.payVip(this, vipId, "ALIPAY", new SimpleCallBack<JsonObject>() {
            @Override
            public void onSuccess(JsonObject jsonObject) {
                LoadingViewDialog.getInstance().dismiss();
                LogUtils.e("支付信息->"+jsonObject.toString());

            }

            @Override
            public void onFailure(Throwable throwable) {
                LoadingViewDialog.getInstance().dismiss();
                LogUtils.e("支付信息失败->"+throwable.toString());
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case RESULT_CODE_VIP1:
                    break;
                case RESULT_CODE_VIP2:
                    showDialog("套餐二：“落地POS”功能开通成功，可前往申请POS",true);
                    break;
                case RESULT_CODE_VIP3:
                    showDialog("套餐三：“自动交易+落地POS”功能开通成功，可前往申请POS",true);
                    break;
            }
            loadData();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upgrade_join_item_view1:
                Intent intent1=new Intent(this,VipInstructionActivity.class);
                intent1.putExtra("text",mVipPackages.get(0).getDesc());
                startActivity(intent1);
                break;
            case R.id.upgrade_join_item_view2:
                Intent intent2=new Intent(this,VipInstructionActivity.class);
                intent2.putExtra("text",mVipPackages.get(1).getDesc());
                startActivity(intent2);
                break;
            case R.id.upgrade_join_item_view3:
                Intent intent3=new Intent(this,VipInstructionActivity.class);
                intent3.putExtra("text",mVipPackages.get(2).getDesc());
                startActivity(intent3);
                break;
        }
    }


    @Override
    public void onChildClick(View parent, View view) {
        if(mVipPackages.size()>0){
            switch (parent.getId()){
                case R.id.upgrade_join_item_view1:
                    Intent intentVip1=new Intent(this,PayOpenVipActivity.class);
                    intentVip1.putExtra("money",mVipPackages.get(0).getPrice()+"");
                    intentVip1.putExtra("vip_id",mVipPackages.get(0).getVpId()+"");
                    startActivity(intentVip1);
                    break;
                case R.id.upgrade_join_item_view2:
                    loadVipData(RESULT_CODE_VIP2);
                    break;
                case R.id.upgrade_join_item_view3:
                    if(!"01".equals(mVipPackages.get(0).getOpenState())){
                        loadVipData(RESULT_CODE_VIP3);
                    }
                    break;
            }
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }
}
