package it.unisa.microapp.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.unisa.microapp.R;
import it.unisa.microapp.data.StringData;
import it.unisa.microapp.editor.SpeechDatePickerDialog;
import it.unisa.microapp.utils.Constants;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class DateActivity extends MAActivity {
	protected int mYear = 1900;
	protected int mMonth = 0;
	protected int mDay = 1;

	@Override
	protected void initialize(Bundle savedInstanceState) {
		

	}

	@Override
	protected void prepare() {
		

	}

	@Override
	protected int onVisible() {
		return 0;
	}

	@Override
	protected View onVisibleView() {
		
		return null;
	}

	@Override
	protected void prepareView(View v) {
		
	}	
	
	@Override
	protected void execute() {
		getDate();
	}	
	
	protected DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			next();
		}
	};

	private void getDate() {
		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		SpeechDatePickerDialog dialog = new SpeechDatePickerDialog(this, DateActivity.this, mDateSetListener, mYear, mMonth, mDay);

		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						next();
					}
				});
			
		dialog.setTitle("select a date for "+mycomponent.getBindings(0));

		dialog.show();
	}

	@Override
	public void initInputs() {
		
	}

    @Override
    protected void resume() {    	
    	 //metodi per speech Vincenzo Savarese
    	
    }		
	
	@Override
	public void beforeNext() {
		SimpleDateFormat format = new SimpleDateFormat(Constants.dateFormat, Locale.getDefault());

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, mYear);
		c.set(Calendar.MONTH, mMonth);
		c.set(Calendar.DAY_OF_MONTH, mDay);
		String dateFormat = format.format(c.getTime());		

		StringData data = new StringData(this.mycomponent.getId(), dateFormat);
		this.application.putData(this.mycomponent, data);
	}

}
