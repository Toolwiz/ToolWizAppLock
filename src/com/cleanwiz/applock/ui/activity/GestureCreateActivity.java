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
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.anim.PopListener;
import com.cleanwiz.applock.ui.widget.LockPatternUtils;
import com.cleanwiz.applock.ui.widget.LockPatternView;
import com.cleanwiz.applock.ui.widget.LockPatternView.Cell;
import com.cleanwiz.applock.ui.widget.LockPatternView.DisplayMode;
import com.cleanwiz.applock.ui.widget.actionview.ActionView;
import com.cleanwiz.applock.ui.widget.actionview.CloseAction;
import com.cleanwiz.applock.ui.widget.actionview.MoreAction;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;
import com.cleanwiz.applock.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class GestureCreateActivity extends BaseActivity {

    public static final String CHANGE_FLAG = "change_flag";
    private boolean changeFlag;

    private static final int ID_EMPTY_MESSAGE = -1;
    private static final String KEY_UI_STAGE = "uiStage";
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";
    private LockPatternView mLockPatternView;
    private Button mFooterRightButton;
    private Button mFooterLeftButton;
    protected TextView mHeaderText;
    protected List<LockPatternView.Cell> mChosenPattern = null;
    private Toast mToast;
    private Stage mUiStage = Stage.Introduction;
    private View mPreviewViews[][] = new View[3][3];
    private View popView;
    private ActionView actionView;
    private ScaleAnimation pop_in;
    private ScaleAnimation pop_out;
    /**
     * The patten used during the help screen to show how to draw a pattern.
     */
    private final List<LockPatternView.Cell> mAnimatePattern = new ArrayList<LockPatternView.Cell>();

    /**
     * The states of the left footer button.
     */
    enum LeftButtonMode {

        Cancel(android.R.string.cancel, true),

        CancelDisabled(android.R.string.cancel, false),

        Retry(R.string.lockpattern_retry_button_text, true),

        RetryDisabled(R.string.lockpattern_retry_button_text, false),

        Gone(ID_EMPTY_MESSAGE, false);

        /**
         * @param text    The displayed text for this mode.
         * @param enabled Whether the button should be enabled.
         */
        LeftButtonMode(int text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    /**
     * The states of the right button.
     */
    enum RightButtonMode {
        Continue(R.string.lockpattern_continue_button_text, true),

        ContinueDisabled(R.string.lockpattern_continue_button_text, false),

        Confirm(R.string.lockpattern_confirm_button_text, true),

        ConfirmDisabled(R.string.lockpattern_confirm_button_text, false),

        Ok(android.R.string.ok, true);

        /**
         * @param text    The displayed text for this mode.
         * @param enabled Whether the button should be enabled.
         */
        RightButtonMode(int text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    /**
     * Keep track internally of where the user is in choosing a pattern.
     */
    protected enum Stage {

        Introduction(R.string.lockpattern_recording_intro_header,
                LeftButtonMode.Cancel, RightButtonMode.ContinueDisabled,
                ID_EMPTY_MESSAGE, true),

        HelpScreen(R.string.lockpattern_settings_help_how_to_record,
                LeftButtonMode.Gone, RightButtonMode.Ok, ID_EMPTY_MESSAGE,
                false),

        ChoiceTooShort(R.string.lockpattern_recording_incorrect_too_short,
                LeftButtonMode.Retry, RightButtonMode.ContinueDisabled,
                ID_EMPTY_MESSAGE, true),

        FirstChoiceValid(R.string.lockpattern_pattern_entered_header,
                LeftButtonMode.Retry, RightButtonMode.Continue,
                ID_EMPTY_MESSAGE, false),

        NeedToConfirm(R.string.lockpattern_need_to_confirm,
                LeftButtonMode.Cancel, RightButtonMode.ConfirmDisabled,
                ID_EMPTY_MESSAGE, true),

        ConfirmWrong(R.string.lockpattern_need_to_unlock_wrong,
                LeftButtonMode.Cancel, RightButtonMode.ConfirmDisabled,
                ID_EMPTY_MESSAGE, true),

        ChoiceConfirmed(R.string.lockpattern_pattern_confirmed_header,
                LeftButtonMode.Cancel, RightButtonMode.Confirm,
                ID_EMPTY_MESSAGE, false);

        /**
         * @param headerMessage  The message displayed at the top.
         * @param leftMode       The mode of the left button.
         * @param rightMode      The mode of the right button.
         * @param footerMessage  The footer message.
         * @param patternEnabled Whether the pattern widget is enabled.
         */
        Stage(int headerMessage, LeftButtonMode leftMode,
              RightButtonMode rightMode, int footerMessage,
              boolean patternEnabled) {
            this.headerMessage = headerMessage;
            this.leftMode = leftMode;
            this.rightMode = rightMode;
            this.footerMessage = footerMessage;
            this.patternEnabled = patternEnabled;
        }

        final int headerMessage;
        final LeftButtonMode leftMode;
        final RightButtonMode rightMode;
        final int footerMessage;
        final boolean patternEnabled;
    }

    private void showToast(CharSequence message) {
        if (null == mToast) {
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }

        mToast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_create);
        // 初始化演示动画
        mAnimatePattern.add(LockPatternView.Cell.of(0, 0));
        mAnimatePattern.add(LockPatternView.Cell.of(0, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(1, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 2));

        mLockPatternView = (LockPatternView) this
                .findViewById(R.id.gesturepwd_create_lockview);
        mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        mLockPatternView.setTactileFeedbackEnabled(true);

        mFooterRightButton = (Button) this.findViewById(R.id.right_btn);
        mFooterLeftButton = (Button) this.findViewById(R.id.reset_btn);
        mFooterRightButton.setOnClickListener(this);
        mFooterLeftButton.setOnClickListener(this);
        initPreviewViews();
        if (savedInstanceState == null) {
            updateStage(Stage.Introduction);
        } else {
            // restore from previous state
            final String patternString = savedInstanceState
                    .getString(KEY_PATTERN_CHOICE);
            if (patternString != null) {
                mChosenPattern = LockPatternUtils
                        .stringToPattern(patternString);
            }
            updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
        }

        changeFlag = getIntent().getBooleanExtra(CHANGE_FLAG, false);
        if (changeFlag) {
            findViewById(R.id.gesturecreate_ll_top).setVisibility(View.GONE);
        }

        // pop
        popView = findViewById(R.id.layout_pop);
        actionView = (ActionView) findViewById(R.id.btn_more);

        initAnim();

    }

    private void initAnim() {

        long durationS = 160;
        AccelerateInterpolator accInterpolator = new AccelerateInterpolator();

        pop_in = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);
        pop_out = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);

        pop_in.setDuration(durationS);
        pop_in.setInterpolator(accInterpolator);
        pop_in.setAnimationListener(new PopListener(popView,
                PopListener.TYPE_IN));

        pop_out.setDuration(durationS);
        pop_out.setInterpolator(accInterpolator);
        pop_out.setAnimationListener(new PopListener(popView,
                PopListener.TYPE_OUT));

    }

    private void btnClickMore() {
        if (View.VISIBLE == popView.getVisibility()) {
            actionView.setAction(new MoreAction(),
                    ActionView.ROTATE_COUNTER_CLOCKWISE);
            popView.clearAnimation();
            popView.startAnimation(pop_out);
        } else {
            actionView.setAction(new CloseAction(),
                    ActionView.ROTATE_COUNTER_CLOCKWISE);
            popView.clearAnimation();
            popView.startAnimation(pop_in);
        }

    }

    private void initPreviewViews() {
        mPreviewViews = new View[3][3];
        mPreviewViews[0][0] = findViewById(R.id.gesturepwd_setting_preview_0);
        mPreviewViews[0][1] = findViewById(R.id.gesturepwd_setting_preview_1);
        mPreviewViews[0][2] = findViewById(R.id.gesturepwd_setting_preview_2);
        mPreviewViews[1][0] = findViewById(R.id.gesturepwd_setting_preview_3);
        mPreviewViews[1][1] = findViewById(R.id.gesturepwd_setting_preview_4);
        mPreviewViews[1][2] = findViewById(R.id.gesturepwd_setting_preview_5);
        mPreviewViews[2][0] = findViewById(R.id.gesturepwd_setting_preview_6);
        mPreviewViews[2][1] = findViewById(R.id.gesturepwd_setting_preview_7);
        mPreviewViews[2][2] = findViewById(R.id.gesturepwd_setting_preview_8);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
        if (mChosenPattern != null) {
            outState.putString(KEY_PATTERN_CHOICE,
                    LockPatternUtils.patternToString(mChosenPattern));
        }
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
            patternInProgress();
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            if (pattern == null)
                return;
            if (mUiStage == Stage.NeedToConfirm) {
                if (mChosenPattern == null)
                    throw new IllegalStateException(
                            "null chosen pattern in stage 'need to confirm");
                if (mChosenPattern.equals(pattern)) {
                    updateStage(Stage.ChoiceConfirmed);
                } else {
                    updateStage(Stage.ConfirmWrong);
                }
            } else if (mUiStage == Stage.Introduction
                    || mUiStage == Stage.ChoiceTooShort
                    || mUiStage == Stage.ConfirmWrong) {
                if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
                    updateStage(Stage.ChoiceTooShort);
                } else {
                    mChosenPattern = new ArrayList<LockPatternView.Cell>(
                            pattern);
                    updateStage(Stage.FirstChoiceValid);
                }
            } else {
                throw new IllegalStateException("Unexpected stage " + mUiStage
                        + " when " + "entering the pattern.");
            }
        }

        public void onPatternCellAdded(List<Cell> pattern) {

        }

        private void patternInProgress() {
            mHeaderText.setText(R.string.lockpattern_recording_inprogress);
            mFooterLeftButton.setEnabled(false);
            mFooterRightButton.setEnabled(false);
        }
    };

    private void updateStage(Stage stage) {
        mUiStage = stage;
        if (stage == Stage.ChoiceTooShort) {
            mHeaderText.setText(getResources().getString(stage.headerMessage,
                    LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
        } else {
            mHeaderText.setText(stage.headerMessage);
        }

        if (stage.leftMode == LeftButtonMode.Gone) {
            mFooterLeftButton.setVisibility(View.GONE);
        } else {
            mFooterLeftButton.setVisibility(View.VISIBLE);
            mFooterLeftButton.setText(stage.leftMode.text);
            mFooterLeftButton.setEnabled(stage.leftMode.enabled);
        }

        mFooterRightButton.setText(stage.rightMode.text);
        mFooterRightButton.setEnabled(stage.rightMode.enabled);

        // same for whether the patten is enabled
        if (stage.patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }

        mLockPatternView.setDisplayMode(DisplayMode.Correct);

        switch (mUiStage) {
            case Introduction:
                mLockPatternView.clearPattern();
                break;
            case HelpScreen:
                mLockPatternView.setPattern(DisplayMode.Animate, mAnimatePattern);
                break;
            case ChoiceTooShort:
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case FirstChoiceValid:
                updateStage(Stage.NeedToConfirm);
                break;
            case NeedToConfirm:
                mLockPatternView.clearPattern();
                break;
            case ConfirmWrong:

                mChosenPattern = null;
                mLockPatternView.clearPattern();
                break;
            case ChoiceConfirmed:
                saveChosenPatternAndFinish();
                break;
        }

    }

    // clear the wrong pattern unless they have started a new one
    // already
    private void postClearPatternRunnable() {
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_btn:

                // TODO
                if (mUiStage.leftMode == LeftButtonMode.Retry) {
                    mChosenPattern = null;
                    mLockPatternView.clearPattern();
                    updateStage(Stage.Introduction);
                } else if (mUiStage.leftMode == LeftButtonMode.Cancel) {
                    // They are canceling the entire wizard
                    finish();
                } else {
                    throw new IllegalStateException(
                            "left footer button pressed, but stage of " + mUiStage
                                    + " doesn't make sense");
                }

                break;
            case R.id.right_btn:
                if (mUiStage.rightMode == RightButtonMode.Continue) {
                    if (mUiStage != Stage.FirstChoiceValid) {
                        throw new IllegalStateException("expected ui stage "
                                + Stage.FirstChoiceValid + " when button is "
                                + RightButtonMode.Continue);
                    }
                    updateStage(Stage.NeedToConfirm);
                } else if (mUiStage.rightMode == RightButtonMode.Confirm) {
                    if (mUiStage != Stage.ChoiceConfirmed) {
                        throw new IllegalStateException("expected ui stage "
                                + Stage.ChoiceConfirmed + " when button is "
                                + RightButtonMode.Confirm);
                    }
                    saveChosenPatternAndFinish();
                } else if (mUiStage.rightMode == RightButtonMode.Ok) {
                    if (mUiStage != Stage.HelpScreen) {
                        throw new IllegalStateException(
                                "Help screen is only mode with ok button, but "
                                        + "stage is " + mUiStage);
                    }
                    mLockPatternView.clearPattern();
                    mLockPatternView.setDisplayMode(DisplayMode.Correct);
                    updateStage(Stage.Introduction);
                }
                break;
        }
    }

    private void saveChosenPatternAndFinish() {
        AppLockApplication.getInstance().getLockPatternUtils()
                .saveLockPattern(mChosenPattern);
        ToastUtils.showToast(R.string.password_set_success);
        AppLockApplication.getInstance().setStartGuide(true);
        startActivity(new Intent(this, LockMainActivity.class));
        SharedPreferenceUtil.editIsNumModel(false);
        finish();
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btn_change:
                changeToNum();
                break;
            case R.id.btn_more:
                btnClickMore();
                break;
            default:
                break;
        }
        super.onClickEvent(view);
    }

    private void changeToNum() {

        Intent intent = new Intent(this, NumberCreateActivity.class);
        intent.putExtra(NumberCreateActivity.CHANGE_FLAG, changeFlag);
        startActivity(intent);
        finish();

    }

}
