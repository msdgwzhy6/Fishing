package com.jude.fishing.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.jude.beam.model.AbsModel;
import com.jude.fishing.model.entities.PersonAvatar;
import com.jude.fishing.model.entities.PersonBrief;
import com.jude.fishing.module.user.UserDetailActivity;
import com.jude.utils.JUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

/**
 * Created by Mr.Jude on 2015/7/8.
 */
public class RongYunModel extends AbsModel {

    public static RongYunModel getInstance() {
        return getInstance(RongYunModel.class);
    }
    public BehaviorSubject<Integer> mNotifyBehaviorSubject = BehaviorSubject.create();
    @Override
    protected void onAppCreate(Context ctx) {
        AccountModel.getInstance().registerAccountUpdate(user -> {
            if (user != null) connectRongYun1(user.getRongToken());
        });
        if (AccountModel.getInstance().getAccount()!=null)
            connectRongYun1(AccountModel.getInstance().getAccount().getRongToken());
    }

    public void loginOut(){
        connectRongYun1("");
    }

    public Subscription registerNotifyCount(Action1<Integer> notify){
        return mNotifyBehaviorSubject.subscribe(notify);
    }

    public void connectRongYun1(String token){
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                JUtils.Log("融云Token失效");
            }

            @Override
            public void onSuccess(String s) {
                JUtils.Log("融云连接成功");
                setRongYun();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                JUtils.Log("融云连接失败：" + errorCode.getValue() + ":" + errorCode.getMessage());
            }
        });
    }

    public void setRongYun(){
        try {
            RongIM.setUserInfoProvider(userId -> {
                PersonAvatar p;
                try{
                    p = AccountModel.getInstance().getPersonAvatar(userId);
                }catch (Exception e){
                    return null;
                }
                return new UserInfo(userId, p.getName(), ImageModel.getInstance().getSmallImage(p.getAvatar()));
            }, true);
            RongIM.setConversationBehaviorListener(new RongIM.ConversationBehaviorListener() {
                @Override
                public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                    Intent i = new Intent(context, UserDetailActivity.class);
                    i.putExtra("id", Integer.parseInt(userInfo.getUserId()));
                    context.startActivity(i);
                    return true;
                }

                @Override
                public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                    return false;
                }

                @Override
                public boolean onMessageClick(Context context, View view, Message message) {
                    return false;
                }

                @Override
                public boolean onMessageLongClick(Context context, View view, Message message) {
                    return false;
                }
            });
            RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new RongIM.OnReceiveUnreadCountChangedListener() {
                @Override
                public void onMessageIncreased(int i) {
                    mNotifyBehaviorSubject.onNext(i);
                }
            }, Conversation.ConversationType.PRIVATE);
        } catch (Exception e) {
            JUtils.Log("融云出错");
        }
    }


    public void updateRongYunPersonBrief(PersonBrief p){
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(p.getUID()+"",p.getName(), Uri.parse(p.getAvatar())));
    }

    public void chatPerson(Context ctx,String id,String title){
//        Intent i = new Intent(ctx, ChatActivity.class);
//        i.putExtra("id",id);
//        i.putExtra("title",title);
//        i.putExtra("type", Conversation.ConversationType.PRIVATE.getName().toLowerCase());
//        ctx.startActivity(i);
        RongIM.getInstance().startPrivateChat(ctx, id, title);
    }

    public void chatGroup(Context ctx,String id,String title){
//        Intent i = new Intent(ctx, ChatActivity.class);
//        i.putExtra("id",id);
//        i.putExtra("title",title);
//        i.putExtra("type", Conversation.ConversationType.GROUP.getName().toLowerCase());
//        ctx.startActivity(i);
        RongIM.getInstance().startGroupChat(ctx,id,title);
    }

    public void chatList(Context ctx){
        RongIM.getInstance().startConversationList(ctx);
        //ctx.startActivity(new Intent(ctx, ChatListActivity.class));
    }
}
