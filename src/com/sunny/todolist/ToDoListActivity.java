package com.sunny.todolist;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class ToDoListActivity extends Activity 
	implements NewItemFragment.OnNewItemAddedListener {
	
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
	}

	@Override
	public void onNewItemAdded(String newItem) {
		ToDoItem newTodoItem = new ToDoItem(newItem);
		todoItems.add(0, newTodoItem);
		aa.notifyDataSetChanged();
	}

}
