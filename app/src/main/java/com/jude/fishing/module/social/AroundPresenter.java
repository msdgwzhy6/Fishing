package com.jude.fishing.module.social;

import android.os.Bundle;

import com.jude.beam.expansion.list.BeamListActivityPresenter;
import com.jude.fishing.model.SocialModel;
import com.jude.fishing.model.bean.PersonBrief;

/**
 * Created by Mr.Jude on 2015/9/23.
 */
public class AroundPresenter extends BeamListActivityPresenter<AroundActivity,PersonBrief>{
    @Override
    protected void onCreate(AroundActivity view, Bundle savedState) {
        super.onCreate(view, savedState);
        onRefresh();
    }

    @Override
    public void onLoadMore() {
        SocialModel.getInstance().getAround(getCurPage()).unsafeSubscribe(getMoreSubscriber());
    }

    @Override
    public void onRefresh() {
        SocialModel.getInstance().getAround(0).unsafeSubscribe(getRefreshSubscriber());
    }
}