package com.sunny.todolist;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ToDoListActivity extends Activity 
	implements NewItemFragment.OnNewItemAddedListener {
	
	private ArrayAdapter<String> aa;
	private ArrayList<String> todoItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getFragmentManager();
		ToDoListFragment todoListFragment = 
				(ToDoListFragment) fm.findFragmentById(R.id.ToDoListFragment);
		
		todoItems = new ArrayList<String>();
		
		int resID = R.layout.item_todolist;
		aa = new ArrayAdapter<>(this, resID, todoItems);
		
		todoListFragment.setListAdapter(aa);
	}

	@Override
	public void onNewItemAdded(String newItem) {
		todoItems.add(newItem);
		aa.notifyDataSetChanged();
	}

}
