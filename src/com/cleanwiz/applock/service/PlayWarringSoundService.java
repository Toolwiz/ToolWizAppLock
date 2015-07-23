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
package com.cleanwiz.applock.service;

import com.cleanwiz.applock.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class PlayWarringSoundService {

	private SoundPool soundPool;
	private Context context;
	
	public PlayWarringSoundService(Context context) {
		this.context = context;
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(context, R.raw.warring01, 1);
	}
	
	public void playSound() {
		soundPool.play(1, 1, 1, 0, 0, 1);
	}
	
}
