package com.nutomic.zertman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.Closeable;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps information about certificates that have been moved to system memory.
 */
public class MovedCertificatesStorage implements Closeable {

	private static final String TAG = "MovedCertificatesStorage";


	private MovedCertificatesHelper mDbHelper;

	public MovedCertificatesStorage(Context context) {
		mDbHelper = new MovedCertificatesHelper(context);
	}

	@Override
	public void close() throws IOException {
		mDbHelper.close();
	}

	public boolean insert(Certificate cert) {
		assert(cert.isSystemCertificate());

		ContentValues cv = new ContentValues();
		cv.put(Table.COLUMN_NAME_FILE_NAME, cert.getFile().getName());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long row = db.insert(Table.TABLE_NAME, null, cv);
		return row != -1;
	}

	public List<Certificate> list() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.query(
				Table.TABLE_NAME, new String[]{Table.COLUMN_NAME_FILE_NAME},
				null, null, null, null, null);
		ArrayList<Certificate> list = new ArrayList<Certificate>(c.getCount());
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); i++) {
			list.add(new Certificate(
					c.getString(c.getColumnIndex(Table.COLUMN_NAME_FILE_NAME)), true));
			c.moveToNext();
		}
		c.close();
		return list;
	}

	public boolean delete(Certificate cert) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = db.delete(Table.TABLE_NAME, Table.COLUMN_NAME_FILE_NAME + " = ?",
				new String[]{cert.getFile().getName()});
		return count == 1;
	}

	public abstract class Table implements BaseColumns {
		public static final String TABLE_NAME = "certificate";
		public static final String COLUMN_NAME_FILE_NAME = "file_name";
	}

	private class MovedCertificatesHelper extends SQLiteOpenHelper {

		private static final int DATABASE_VERSION = 1;

		private static final String DATABASE_NAME = "moved_certificates.db";

		public MovedCertificatesHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + Table.TABLE_NAME + " (" +
					Table._ID + " INTEGER PRIMARY KEY," +
					Table.COLUMN_NAME_FILE_NAME + " TEXT UNIQUE)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

}
