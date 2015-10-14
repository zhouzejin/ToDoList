package com.sunny.todolist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ToDoContentProvider extends ContentProvider {
	
	public static final Uri CONTENT_URI = 
			Uri.parse("content://com.sunny.todoprovider/todoitems");
	public static final String KEY_ID = "_id";
	public static final String KEY_TASK = "task";
	public static final String KEY_CREATION_DATE = "creation_date";
	
	private static final String TAG = "ToDoContentProvider";
	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;
	
	private static final UriMatcher uriMatcher;
	/**
	 * Populate the UriMatcher object, where a URI ending in 'todoitems' will 
	 * correspond to a request for all items, and 'todoitems/[rowID]' 
	 * represents a single row.
	 */
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.sunny.todolist", "todoitems", ALLROWS);
		uriMatcher.addURI("com.sunny.todolist", "todoitem/#", SINGLE_ROW);
	}
	
	private MySQLiteOpenHelper myOpenHelper;

	public ToDoContentProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCreate() {
		myOpenHelper = new MySQLiteOpenHelper(getContext(), 
				MySQLiteOpenHelper.DATABASE_NAME, null, 
				MySQLiteOpenHelper.DATABASE_VERSION);
		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = myOpenHelper.getReadableDatabase();
		
		String groupBy = null;
		String having = null;
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);
		
		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(KEY_ID + "=" + rowID);
			
		default:
			break;
		}
		
		Cursor cursor = queryBuilder.query(db, projection, selection, 
				selectionArgs, groupBy, having, sortOrder);
		
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// Return a string that identifies the MIME type
	    // for a Content Provider URI
		switch (uriMatcher.match(uri)) {
		case ALLROWS:
			return "vnd.android.cursor.dir/vnd.sunny.todos";
		case SINGLE_ROW:
			return "vnd.android.cursor.item/vnd.sunny.todos";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		
		// To add empty rows to your database by passing in an empty Content Values
	    // object, you must use the null column hack parameter to specify the name of
	    // the column that can be set to null.
		String nullColumnHack = null;
		
		long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE, 
				nullColumnHack, values);
		if (id > -1) {
			Uri insertId = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(insertId, null);
			return insertId;
		} else 
			return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		
		// If this is a row URI, limit the deletion to the specified row.
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowID + 
					(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
			
		default:
			break;
		}
		
		// To return the number of deleted items, you must specify a where
	    // clause. To delete all rows and return a value, pass in "1".
		if (selection == null)
			selection = "1";
		
		int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE, 
				selection, selectionArgs);
		
		// Notify any observers of the change in the data set.
		getContext().getContentResolver().notifyChange(uri, null);
		
		return deleteCount;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = myOpenHelper.getWritableDatabase();
		
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowID + 
					(!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
			
		default:
			break;
		}
		
		int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE, 
				values, selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updateCount;
	}
	
	private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
		
		private static final String DATABASE_NAME = "todoDatabase.db";
		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_TABLE = "todoItemTable";
		
		/**
		 * 创建数据库的SQL语句
		 */
		private static final String DATABASE_CREATE = 
				"create table " + DATABASE_TABLE + " (" + 
				KEY_ID + " integer primary key autoincrement, " + 
				KEY_TASK + " text not null, " + 
				KEY_CREATION_DATE + " long);";

		public MySQLiteOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// 记录版本升级
			Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + 
					" to " + newVersion + ", which will destory all old data!");
			
			// 最简单的升级方式-删除旧的表，创建新表
			String sql = "DROP TABLE IF IT EXISTS " + DATABASE_CREATE;
			Log.i(TAG, sql);
			db.execSQL(sql);
			onCreate(db);
		}
		
	}

}
