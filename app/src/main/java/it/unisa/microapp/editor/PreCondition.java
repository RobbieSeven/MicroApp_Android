package it.unisa.microapp.editor;

import android.os.Parcel;
import android.os.Parcelable;

public class PreCondition implements Parcelable{
	
	private boolean check;
	private int condition;
	private int operator;
	private String value;
	
	public PreCondition(boolean c,int cond,int oper,String text){
		check = c;
		condition=cond;
		operator = oper;
		value = text;
	}
	
	public PreCondition(){
		check = false;
		condition = 0;
		operator = 0;
		value="";
	}
	
	public PreCondition(Parcel p){
		check = p.readByte() == 1 ;
		condition = p.readInt();
		operator = p.readInt();
		value = p.readString();
	}
	
	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
	
	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public int getOperator() {
		return operator;
	}
	
	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String v) {
		this.value = v;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	
	@Override
	public void writeToParcel(Parcel p, int arg1) {
		p.writeByte((byte) (check ? 1 : 0));
		p.writeInt(condition);
		p.writeInt(operator);
		p.writeString(value);
	}
	
	public static final Creator<PreCondition> CREATOR =
			new Creator<PreCondition>() {
		
			public PreCondition createFromParcel(Parcel in) {
			return new PreCondition(in);
			}
			
			public PreCondition[] newArray(int size) {
			return new PreCondition[size];
			}
	};
}