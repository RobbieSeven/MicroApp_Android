package it.unisa.microapp.webservice.view;

import it.unisa.microapp.webservice.entry.MAEntry;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class LinearView extends ComplexView {

	public LinearView(Context context) {
		super(context);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getGUIValues() {
		List<MAEntry<String, Object>> list = new LinkedList<MAEntry<String, Object>>();

		for (int i = 0; i < this.getChildCount(); i++) {
			String name = (String) this.getChildAt(i).getTag();

			if (this.getChildAt(i) instanceof ArrayElementView) {
				ArrayElementView array = (ArrayElementView) this.getChildAt(i);
				Object[] obj = (Object[]) array.getGUIValues();

				for (int j = 0; j < obj.length; j++)
					list.add((MAEntry<String, Object>) obj[j]);
			}

			else if (this.getChildAt(i) instanceof ComplexView) {
				ComplexView v = (ComplexView) this.getChildAt(i);
				Object obj = v.getGUIValues();
				list.add(new MAEntry<String, Object>(name, obj));
			} else {
				Object obj = extractDataFromView(this.getChildAt(i));

				if (obj != null)
					list.add(new MAEntry<String, Object>(name, obj));
			}
		}

		return list.toArray();
	}

	private Object extractDataFromView(View childAt) {
		if (childAt instanceof EditText)
			return "" + ((EditText) childAt).getText().toString();

		if (childAt instanceof Spinner)
			return "" + ((Spinner) childAt).getSelectedItem().toString();

		if (childAt instanceof DatePicker) {
			DatePicker d = (DatePicker) childAt;
			String date = d.getYear() + "-" + (d.getMonth() + 1) + "-" + d.getDayOfMonth();
			return "" + date;
		}

		if (childAt instanceof TimePicker) {
			TimePicker t = (TimePicker) childAt;
			String time = t.getCurrentHour() + ":" + t.getCurrentMinute() + ":00";
			return "" + time;
		}

		if (childAt instanceof CheckBox) {
			CheckBox c = (CheckBox) childAt;
			String bool;
			if (c.isChecked())
				bool = "true";
			else
				bool = "false";

			return "" + bool;
		}

		return null;
	}

}
