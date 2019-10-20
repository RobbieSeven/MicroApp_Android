package it.unisa.microapp.activities;

import it.unisa.microapp.R;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BlankActivity extends MAActivity {
	TextView label;
	
	@Override
	protected void initialize(Bundle savedInstanceState) {
	}

	@Override
	protected void prepare() {
	}

	@Override
	protected int onVisible() {
		return R.layout.nodeployed;
	}

	@Override
	protected View onVisibleView() {
		return null;
	}

	@Override
	protected void prepareView(View v) {
		label = (TextView) this.findViewById(R.id.tv);
		if(label != null) {
			label.setText(mycomponent.getType());
		}
	}	
	
	@Override
	protected void execute() {
	}	
	
	@Override
	protected void restart() {
	}

	@Override
	protected void resume() {
	}

	@Override
	protected void pause() {
	}

	@Override
	protected void onCondition(boolean state) {
	}

	@Override
	protected void initInputs() {
	}

	@Override
	protected void beforeNext() {
	}

	@Override
	protected void destroy() {
	}
}


