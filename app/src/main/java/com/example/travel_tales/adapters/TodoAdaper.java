package com.example.travel_tales.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_tales.R;
import com.example.travel_tales.activities.AddNewTodo;
import com.example.travel_tales.activities.TodoActivity;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.Todo;

import java.util.List;

public class TodoAdaper extends RecyclerView.Adapter<TodoAdaper.MyViewHolder> {

    // Creating variables
    private List<Todo> mList;

    private TodoActivity todoActivity;
    private DBHelper dbHelper;

    public TodoAdaper(DBHelper dbhelper, TodoActivity todoActivity){
        this.todoActivity = todoActivity;
        this.dbHelper = dbhelper;
    }

    @NonNull
    @Override
    public TodoAdaper.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdaper.MyViewHolder holder, int position) {
        final Todo todoItem = mList.get(position);
        holder.mCheckBox.setText(todoItem.getTodoTitle());
        holder.mCheckBox.setChecked(toBoolean(todoItem.getStatus()));
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if(isChecked){
                  dbHelper.updateStatus(todoItem.getId(), 1);
              }
              else {
                  dbHelper.updateStatus(todoItem.getId(), 0);
              }
            }
        });
    }

    public boolean toBoolean(int num){
        return num !=0;
    }

    // Creating context to return
    public Context getContext(){
        return todoActivity;
    }

    public void setTodoItem(List<Todo> mList){
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void deleteTodoItem(int position){
        Todo item = mList.get(position);
        dbHelper.deleteTodo(item.getId());
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void editTodoItem(int position){
        Todo todoItem = mList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", todoItem.getId());
        bundle.putString("todo", todoItem.getTodoTitle());

        // pass the data from activity to fragment
        AddNewTodo newTodo = new AddNewTodo();
        newTodo.setArguments(bundle);
        newTodo.show(todoActivity.getSupportFragmentManager(), newTodo.getTag());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.todoCheckbox);
        }
    }
}

