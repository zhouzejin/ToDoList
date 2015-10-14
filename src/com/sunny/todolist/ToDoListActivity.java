package com.sunny.todolist;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

public class ToDoListActivity extends Activity 
	implements NewItemFragment.OnNewItemAddedListener, LoaderManager.LoaderCallbacks<Cursor> {
	
	private ToDoItemAdapter aa;
	private ArrayList<ToDoItem> todoItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getFragmentManager();
		ToDoListFragment todoListFragment = 
				(ToDoListFragment) fm.findFragmentById(R.id.ToDoListFragment);
		
		todoItems = new ArrayList<ToDoItem>();
		
		int resID = R.layout.item_todolist;
		aa = new ToDoItemAdapter(this, resID, todoItems);
		
		todoListFragment.setListAdapter(aa);
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onNewItemAdded(String newItem) {
		/*ToDoItem newTodoItem = new ToDoItem(newItem);
		todoItems.add(0, newTodoItem);
		aa.notifyDataSetChanged();*/
		
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(ToDoContentProvider.KEY_TASK, newItem);
		cr.insert(ToDoContentProvider.CONTENT_URI, values);
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(this, 
				ToDoContentProvider.CONTENT_URI, null, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int keyTaskIndex = data.getColumnIndexOrThrow(ToDoContentProvider.KEY_TASK);
		
		todoItems.clear();
		while (data.moveToNext()) {
			ToDoItem newItem = new ToDoItem(data.getString(keyTaskIndex));
			todoItems.add(newItem);
		}
		
		aa.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}

}
