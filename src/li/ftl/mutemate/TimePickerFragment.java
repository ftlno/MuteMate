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

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		return new TimePickerDialog(getActivity(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(final TimePicker view, int hourOfDay, int minute) {
		final Calendar c = Calendar.getInstance();

		int nowHour = c.get(Calendar.HOUR_OF_DAY);
		int nowMinute = c.get(Calendar.MINUTE);

		String now = "";
		if (nowHour < 10) {
			now += "0";
		}
		now += nowHour + ":";
		if (nowMinute < 10) {
			now += "0";
		}
		now += nowMinute;
		String then = "";
		if (hourOfDay < 10) {
			then += "0";
		}
		then += hourOfDay + ":";
		if (minute < 10) {
			then += "0";
		}
		then += minute;

		MainActivity.total = now + " - " + then;
		minute -= nowMinute;
		hourOfDay -= nowHour;

		MainActivity.hours = hourOfDay;
		if (minute > 0) {
			--minute;
		}
		MainActivity.minutes = minute;
		MainActivity.startstopButton.setText("Start");
	}
}