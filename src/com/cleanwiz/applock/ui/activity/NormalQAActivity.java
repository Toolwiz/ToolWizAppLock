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

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LogUtil;

public class NormalQAActivity extends BaseActivity {

	private ListView qaListView;
	private List<String> items = new ArrayList<String>();
	private LayoutInflater mInflater;
	
	private BaseAdapter adapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textView = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_normalqa, null);
				textView = (TextView)convertView.findViewById(R.id.qa_detailstring);
				convertView.setTag(textView);
			} else {
				textView = (TextView)convertView.getTag();
			}
			if (textView != null) {
				LogUtil.e("colin", "kkkkkk:"+items.get(position));
				textView.setText(items.get(position));
			}
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return items.get(position);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_normalqa);
		this.mInflater = LayoutInflater.from(this);
		items.add(getString(R.string.noamalqa_content1));
		items.add(getString(R.string.noamalqa_content2));
		items.add(getString(R.string.noamalqa_content3));
		qaListView = (ListView)findViewById(R.id.qalistview);
		qaListView.setAdapter(adapter);
	}
	
	public void onClickEvent(android.view.View view) {
		switch (view.getId()) {
		case R.id.btn_back:
		{
			finish();
			break;
		}
		default:
			break;
		}
		super.onClickEvent(view);
	};
}
