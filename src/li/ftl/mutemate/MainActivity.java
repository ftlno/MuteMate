/**
 * Copyright (C) Fredrik Thorbjørnsen Lillejordet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package li.ftl.mutemate;

import android.app.Activity;
import android.app.DialogFragment;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable {

	@SuppressWarnings("unused")
	private static boolean threadRunning;
	private volatile Thread runner;
	private final Handler mHandler = new Handler();
	static AudioManager audioManager;
	public static int hours = 0;
	public static int minutes = 0;
	public static int seconds = 0;
	public static String total = "";
	private static TextView totalDisplay, hourDisplay, minDisplay, secDisplay;
	public static Button startstopButton;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (runner == null) {
			startstopButton.setText("Start");
		} else {
			startstopButton.setText("Abort");
		}
	}

	private void init() {
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		totalDisplay = (TextView) findViewById(R.id.total);
		hourDisplay = (TextView) findViewById(R.id.hours);
		minDisplay = (TextView) findViewById(R.id.minutes);
		secDisplay = (TextView) findViewById(R.id.seconds);

		startstopButton = (Button) findViewById(R.id.startstop);
		startstopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				if (startstopButton.getText().toString().equals("Start")) {
					muteSound();
				} else {
					unmuteSound();
				}
			}
		});
	}

	public void updateDisplay() {
		String hourText = "";
		if (hours < 10) {
			hourText += "0";
		}
		hourText += hours;
		String minuteText = "";
		if (minutes < 10) {
			minuteText += "0";
		}
		minuteText += minutes;

		String secondText = "";
		if (seconds < 10) {
			secondText += "0";
		}
		secondText += seconds;
		hourDisplay.setText(hourText);
		minDisplay.setText(minuteText);
		secDisplay.setText(secondText);
	}

	private void showMessageToUser(final String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void showTimePickerDialog(final View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	private void muteSound() {
		if (durationValid()) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			startThread();
			totalDisplay.setText(total);
			startstopButton.setText("Abort");
			startstopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stop, 0, 0, 0);
		} else {
			showMessageToUser("Sorry, invalid time");
		}
	}

	public void unmuteSound() {
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		startstopButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.start, 0, 0, 0);
		startstopButton.setText("Start");
		minutes = 0;
		seconds = 0;
		hours = 0;
		hourDisplay.setText("00");
		minDisplay.setText("00");
		secDisplay.setText("00");
		totalDisplay.setText("");
		stopThread();
	}

	public boolean durationValid() {
		if ((minutes > 0 || hours > 0)) {
			return true;
		}
		return false;
	}

	public synchronized void startThread() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public synchronized void stopThread() {
		threadRunning = false;
		if (runner != null) {
			Thread old = runner;
			runner = null;
			old.interrupt();
		}
	}

	@Override
	public void run() {
		threadRunning = true;
		while (Thread.currentThread() == runner) {
			if (minutes == 0) {
				hours--;
				minutes = 59;
			}
			do {
				do {
					seconds = 59;
					while (seconds > -1) {
						try {
							Thread.sleep(1000);
							mHandler.post(new Runnable() {
								public void run() {
									updateDisplay();
								}
							});
						} catch (Exception e) {
						}
						seconds--;
					}
					if (minutes > 0) {
						minutes--;
					}
				} while (minutes > 0);
				minutes = 59;
				hours--;

			} while (hours > -1);
			try {
				mHandler.post(new Runnable() {
					public void run() {
						unmuteSound();
					}
				});
			} catch (Exception e) {
			}
		}
	}
}
