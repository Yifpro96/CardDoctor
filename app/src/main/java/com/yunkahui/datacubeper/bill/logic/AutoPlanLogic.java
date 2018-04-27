package com.yunkahui.datacubeper.bill.logic;

import android.content.Context;

import com.hellokiki.rrorequest.HttpManager;
import com.hellokiki.rrorequest.SimpleCallBack;
import com.yunkahui.datacubeper.common.api.ApiService;
import com.yunkahui.datacubeper.common.bean.BaseBean;
import com.yunkahui.datacubeper.common.bean.GeneratePlan;
import com.yunkahui.datacubeper.common.bean.GeneratePlanItem;
import com.yunkahui.datacubeper.common.utils.RequestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoPlanLogic {

    public void confirmAutoPlan(Context context, int cardId, String planDatas, SimpleCallBack<BaseBean> callBack){
        Map<String,String> params= RequestUtils.newParams(context)
                .addParams("bankcard_id",String.valueOf(cardId))
                .addParams("planning_datas",planDatas)
                .addParams("batch_sn","1")
                .addParams("version","v1.1")
                .create();
        HttpManager.getInstance().create(ApiService.class).confirmAutoPlan(params)
                .compose(HttpManager.<BaseBean>applySchedulers()).subscribe(callBack);
    }

    public void generateAutoPlan(Context context, int cardId, String totalMoney, String repayDates, String totalCount, SimpleCallBack<BaseBean<GeneratePlan>> callBack){
        Map<String,String> params= RequestUtils.newParams(context)
                .addParams("bank_card_id",String.valueOf(cardId))
                .addParams("repay_total_money",totalMoney)
                .addParams("repay_dates",repayDates)
                .addParams("repay_total_count",totalCount)
                .addParams("version","v1.1")
                .create();
        HttpManager.getInstance().create(ApiService.class).generateAutoPlan(params)
                .compose(HttpManager.<BaseBean<GeneratePlan>>applySchedulers()).subscribe(callBack);
    }

    public List<GeneratePlanItem> parsingJSONForAutoPlan(BaseBean<GeneratePlan> baseBean) {
        List<GeneratePlanItem> list = new ArrayList<>();
        GeneratePlanItem item;
        for (int x = 0; x < baseBean.getRespData().getPlanningList().size(); x++) {
            List<GeneratePlan.PlanningListBean.DetailsBean> details = baseBean.getRespData().getPlanningList().get(x).getDetails();
            for (int y = 0; y < details.size(); y++) {
                GeneratePlan.PlanningListBean.DetailsBean detailsBean = details.get(y);
                item = new GeneratePlanItem();
                item.setType(1);
                item.setGroup(y);
                item.setSection(-1);
                item.setMoney(detailsBean.getRepayment().getMoney());
                item.setTimeStamp(detailsBean.getRepayment().getTime());
                list.add(item);
                for (int z = 0; z < detailsBean.getConsumption().size(); z++) {
                    item = new GeneratePlanItem();
                    item.setType(0);
                    item.setGroup(y);
                    item.setSection(z);
                    GeneratePlan.PlanningListBean.DetailsBean.ConsumptionBean consumptionBean = detailsBean.getConsumption().get(0);
                    item.setMoney(consumptionBean.getMoney());
                    item.setTimeStamp(consumptionBean.getTime());
                    item.setMccType(consumptionBean.getMccType());
                    list.add(item);
                }
            }
        }
        return list;
    }
}
