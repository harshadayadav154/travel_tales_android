package com.example.travel_tales.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.travel_tales.adapters.OnDialogCloseListener;
import com.example.travel_tales.adapters.RecyclerViewTouchHelper;
import com.example.travel_tales.adapters.TodoAdaper;
import com.example.travel_tales.databinding.ActivityTodoBinding;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodoActivity extends AppCompatActivity implements OnDialogCloseListener, View.OnClickListener {

    ActivityTodoBinding todoBinding;

    private RecyclerView mRecyclerView;
    private FloatingActionButton fabAdd;

    private DBHelper dbHelper;

    private List<Todo> mList;

    private TodoAdaper todoAdaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todoBinding = ActivityTodoBinding.inflate(getLayoutInflater());
        View view = todoBinding.getRoot();
        setContentView(view);

        init();
    }

    private void init(){
        // initializing components
        mRecyclerView = findViewById(todoBinding.todoRecycler.getId());
        fabAdd = findViewById(todoBinding.addTodoFab.getId());
        dbHelper = new DBHelper(TodoActivity.this);
        mList = new ArrayList<>();
        todoAdaper = new TodoAdaper(dbHelper, TodoActivity.this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(todoAdaper);

        mList = dbHelper.getAllTodos(1);
        Collections.reverse(mList);
        todoAdaper.setTodoItem(mList);

        registerEventListeners();


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(todoAdaper));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void registerEventListeners() {

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == todoBinding.addTodoFab.getId()){
                    AddNewTodo.newInstance().show(getSupportFragmentManager(), AddNewTodo.TAG);
                }
            }
        });
        todoBinding.btnGoToHome.setOnClickListener(this);
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList = dbHelper.getAllTodos(1);
        Collections.reverse(mList);
        todoAdaper.setTodoItem(mList);
        todoAdaper.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}