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

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.StringUtils;
import com.cleanwiz.applock.utils.ToastUtils;

public class SecretCheckActivity extends BaseActivity {

    public static final String COME_FROM_LOCK = "come_from_lock";

    private AppLockApplication application = AppLockApplication.getInstance();
    private TextView tv_question;
    private EditText answerEditText;

    private boolean unlockFlag = false;
    private boolean comeFromLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_check);

        comeFromLock = getIntent().getBooleanExtra(COME_FROM_LOCK, false);

        tv_question = (TextView) findViewById(R.id.tv_question);
        answerEditText = (EditText) findViewById(R.id.secretanswer);

        tv_question.setText(application.getSecretQuestionString());
    }

    @Override
    protected void onStop() {
        if (!unlockFlag && comeFromLock) {
            AppLockApplication.getInstance().goHome(this);
        }
        super.onStop();
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btn_menu:
                if (!onBack())
                    finish();
                break;
            case R.id.btn_check:
                int val = checkSecret();
                if (val == 1) {
                    if (onBack())
                        finish();
                } else if (val == -1)
                    finish();
                break;
            default:
                break;
        }
        super.onClickEvent(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onBack()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 返回上一个界面
     *
     * @return 是否截断默认模式
     */
    private boolean onBack() {
        if (getIntent().getBooleanExtra("fromUnlock", false)) {
            Intent intent = new Intent(SecretCheckActivity.this, GestureUnlockActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 检测密保问题
     *
     * @return 是否截断(0不动 1截断 -1 默认)
     */
    private int checkSecret() {

        String answer = null;
        try {
            answer = answerEditText.getText().toString();
        } catch (Exception e) {
            answer = null;
        }
        boolean result = false;
        if (TextUtils.isEmpty(answer)) {
            ToastUtils.showToast(R.string.password_answer_null_toast);
            return 0;
        } else {
            result = StringUtils.toMD5(answer).equals(
                    application.getSecretAnswerString());
        }
        if (result) {
            Intent intent = new Intent(this, GestureCreateActivity.class);
            intent.putExtra(GestureCreateActivity.CHANGE_FLAG, true);
            startActivity(intent);
            unlockFlag = true;
            ToastUtils.showToast(R.string.lock_check_toast_success);
            return -1;
        } else {
            ToastUtils.showToast(R.string.lock_check_toast_error);
            return 0;
        }
    }
}
