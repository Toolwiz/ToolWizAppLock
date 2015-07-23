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

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.service.LookMyPrivateService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.adapter.AppLookMyPrivateAdapter;
import com.cleanwiz.applock.ui.widget.actionview.ActionView;
import com.gc.materialdesign.widgets.SnackBar;

public class LookMyPrivateActivity extends BaseActivity {

    private LookMyPrivateActivity mContext;
    private AppLookMyPrivateAdapter adapter;
    private ListView listView;
    private ActionView clearButton;
    private LinearLayout noshowLayout;
    private LookMyPrivateService lookMyPrivateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookmyprivate);
        setStatusBarMargin(findViewById(R.id.layout_root));

        lookMyPrivateService = new LookMyPrivateService(getApplicationContext());

        mContext = this;

        clearButton = (ActionView) findViewById(R.id.btn_clear);
        clearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                SnackBar snackbar = new SnackBar(LookMyPrivateActivity.this, getString(R.string.clear_all_log), getString(R.string.lock_ok), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lookMyPrivateService.clearLookMyPrivate();
                        adapter.looMyPrivates = lookMyPrivateService
                                .getAllLookMyPrivates();
                        adapter.notifyDataSetChanged();
                        if (lookMyPrivateService.getAllLookMyPrivates().size() == 0) {
                            noshowLayout.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                            clearButton.setVisibility(View.INVISIBLE);
                        } else {
                            noshowLayout.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            clearButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
                snackbar.setDismissTimer(1500);
                snackbar.show();
            }
        });
        listView = (ListView) findViewById(R.id.myprivatelistview);
        noshowLayout = (LinearLayout) findViewById(R.id.myprivate_noshow);
        if (lookMyPrivateService.getAllLookMyPrivates().size() == 0) {
            noshowLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            clearButton.setVisibility(View.INVISIBLE);
        } else {
            noshowLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
        }
        adapter = new AppLookMyPrivateAdapter(
                lookMyPrivateService.getAllLookMyPrivates(),
                getApplicationContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onClickEvent(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.btn_menu: {
                finish();
                break;
            }
            default:
                break;
        }
        super.onClickEvent(view);
    }
}
