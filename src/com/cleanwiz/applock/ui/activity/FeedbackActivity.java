/*******************************************************************************
 * Copyright (c) 2015 btows.com.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.cleanwiz.applock.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LogUtil;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.util.List;

public class FeedbackActivity extends BaseActivity {

    private ListView mListView;
    private FeedbackAgent mAgent;
    private Conversation mComversation;
    private Context mContext;
    private ReplyAdapter adapter;
    private List<Reply> mReplyList;
    private ImageView sendBtn;
    private EditText inputEdit;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final String TAG = FeedbackActivity.class.getName();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_fb_activity_conversation);
        setStatusBarMargin(findViewById(R.id.layout_root));
        mContext = this;

        initView();
        mAgent = new FeedbackAgent(this);
        mComversation = mAgent.getDefaultConversation();
        adapter = new ReplyAdapter();
        mListView.setAdapter(adapter);
        sync();

        clearReplySize();
    }

    /**
     * 清除新消息记录
     */
    private void clearReplySize() {
        AppLockApplication appLockApplication = AppLockApplication.getInstance();
        appLockApplication.setReplySize(0);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.fb_reply_list);
        sendBtn = (ImageView) findViewById(R.id.fb_send_btn);
        inputEdit = (EditText) findViewById(R.id.fb_send_content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fb_reply_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.bg_red, R.color.text_password_wrong, R.color.lock_bg_blue);
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = inputEdit.getText().toString();
                inputEdit.getEditableText().clear();
                if (!TextUtils.isEmpty(content)) {
                    mComversation.addUserReply(content);//添加到会话列表
                    mHandler.sendMessage(new Message());
                    sync();
                }

            }
        });

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });
    }

    // 数据同步
    private void sync() {

        mComversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {

            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (replyList == null || replyList.size() < 1) {
                    return;
                }
                mHandler.sendMessage(new Message());
                mListView.setSelection(mComversation.getReplyList().size());
            }
        });
    }

    // adapter
    class ReplyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            LogUtil.e("colin", "getCount:" + mComversation.getReplyList().size());
            return mComversation.getReplyList().size() + 1;
        }

        @Override
        public Object getItem(int arg0) {
            return mComversation.getReplyList().get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0;
            else {
                Reply reply = mComversation.getReplyList().get(position - 1);
                if (Reply.TYPE_DEV_REPLY.equals(reply.type))
                    return 0;
                else
                    return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (position == 0) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.fb_custom_item, null);
                    holder = new ViewHolder();
                    holder.reply_item = (TextView) convertView.findViewById(R.id.fb_reply_item);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.reply_item.setText(R.string.default_reply);
            } else {
                Reply reply = mComversation.getReplyList().get(position - 1);
                if (convertView == null) {
                    if (Reply.TYPE_DEV_REPLY.equals(reply.type)) {
                        convertView = LayoutInflater.from(mContext).inflate(
                                R.layout.fb_custom_item, null);
                    } else {
                        convertView = LayoutInflater.from(mContext).inflate(
                                R.layout.fb_custom_replyitem, null);
                    }
                    holder = new ViewHolder();
                    holder.reply_item = (TextView) convertView.findViewById(R.id.fb_reply_item);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.reply_item.setText(reply.content);
            }

            return convertView;
        }


        class ViewHolder {
            TextView reply_item;
        }
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btn_menu:
                finish();
                break;

            default:
                break;
        }
        super.onClickEvent(view);
    }

}
