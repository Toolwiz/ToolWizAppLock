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
package com.cleanwiz.applock.files.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.RelativeLayout;
import com.cleanwiz.applock.R;
import com.gc.materialdesign.utils.Utils;

/**
 * 扩展展示分组
 * Created by dev on 2015/5/7.
 */
public class BGridView extends GridView {

    public static final int spacing = 4;
    public static final int margin = 4;

    public BGridView(Context context) {
        super(context);
    }

    public BGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 设置GridView边距
     *
     * @param wm
     * @param spacing (dp)
     * @param margin  (dp)
     * @return
     */
    public int setGridView(WindowManager wm, int spacing, int margin) {
        int width = wm.getDefaultDisplay().getWidth();

        GridView gridView = (GridView) findViewById(R.id.hide_view_list);

        spacing = Utils.dpToPx(spacing, getResources());
        margin = Utils.dpToPx(margin, getResources());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins(margin, margin, margin, margin);

        this.setHorizontalSpacing(spacing);
        this.setVerticalSpacing(spacing);

        this.setLayoutParams(lp);
        return (width - margin * 2 - spacing * 2) / 3;
    }

    /**
     * 设置分组列表间距
     */
    public int setGridView(WindowManager wm) {
        return setGridView(wm, BGridView.spacing, BGridView.margin);
    }
}
