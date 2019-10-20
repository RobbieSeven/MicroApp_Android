package it.unisa.microapp.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBvoti {

	private Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBvoti(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	public DBvoti open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	public void close() {
		DBHelper.close();
		db.close();
	}

	public long insertCommand(String nome, int bool) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("Id_app", nome);
		initialValues.put("Votato", bool);
		long l = 0;

		l = db.insert("Voti", null, initialValues);
		return l;
	}

	public Cursor getAllL() {
		return db.query("Voti", new String[] { "Id_app","Votato"}, null,null, null, null, null);
	}

	public boolean update(String address, int b) {
		ContentValues args = new ContentValues();
		args.put("Votato", b);
		return db.update("Voti", args, "Id_app" + " LIKE " + "'"+ address + "'", null) > 0;
	}

	public void dropRaw() {
		db.execSQL("drop table if exists Voti;");
	}

	public void createTable(){
		db.execSQL("CREATE TABLE Voti (Id_app TEXT PRIMARY KEY, Votato INT not null);");
	}

	public Cursor getVoto(String nome_app ) throws SQLException {
		Cursor mCursor = db.query("Voti",new String[] { "Votato" }, "Id_app LIKE '" + nome_app +"'",null, null, null, null, null);
		mCursor.moveToFirst();
		return mCursor;
	}
}
