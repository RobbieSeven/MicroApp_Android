package it.unisa.microapp.store;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;

public class MyAlertDialog extends AlertDialog.Builder{

	private Button button;
	
	public Button getButton() {
		return button;
	}

	public void setB(Button button) {
		this.button = button;
	}

	protected MyAlertDialog(Context context) {
		super(context);
	}

}
