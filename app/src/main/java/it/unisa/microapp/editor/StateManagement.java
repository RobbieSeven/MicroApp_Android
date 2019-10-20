package it.unisa.microapp.editor;

import java.util.HashSet;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

public class StateManagement implements Parcelable {

	protected String allStates;
	protected String nowState;
	protected boolean stateMod;

	public StateManagement(String all, String v, boolean mod) {
		allStates = all;
		nowState = v;
		stateMod = mod;
	}

	public StateManagement() {
		allStates = "";
		nowState = "";
		stateMod = false;
	}

	public StateManagement(String all, String v) {
		allStates = all;
		nowState = v;
		stateMod = false;
	}

	public StateManagement(Parcel p) {
		allStates = p.readString();
		nowState = p.readString();
	}

	public String getAllStates() {
		return allStates;
	}

	public Set<String> getSetStates() {
		Set<String> all = new HashSet<String>();

		String[] divide = allStates.split("/");

		for (String str : divide) {
			all.add(str);
		}

		return all;
	}

	public String[] getArrayStates() {
		String[] all = null;
		
		if(!allStates.equals("visible")) {
			String[] divide = allStates.split("/");
			all = new String[divide.length];
		
			for(int z=0; z<divide.length; z++)
				all[z] = new String(divide[z]);
		}
		else {
			all = new String[1];
			all[0] = new String(allStates);
		}
		
		return all;
	}	
	
	
	public void setAllStates(String sts) {
		this.allStates = sts;
	}

	public String getNowState() {
		return nowState;
	}

	public void setNowState(String nowState) {
		this.nowState = nowState;
	}

	public int getSelectedState() {
		if (nowState.equals("visible"))
			return 0;
		else if (nowState.equals("hidden"))
			return 1;
		return 2;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(allStates);
		arg0.writeString(nowState);
	}

	public static final Creator<StateManagement> CREATOR = new Creator<StateManagement>() {

		public StateManagement createFromParcel(Parcel in) {
			return new StateManagement(in);
		}

		public StateManagement[] newArray(int size) {
			return new StateManagement[size];
		}
	};

}
