package com.example.travel_tales.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.travel_tales.R;
import com.example.travel_tales.adapters.OnDialogCloseListener;
import com.example.travel_tales.db.DBHelper;
import com.example.travel_tales.models.Todo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTodo extends BottomSheetDialogFragment {


    public static final String TAG = "AddNewTodo";

    // initializing widgets
    private EditText mEditText;
    private Button mSaveButton;

    // Create dbHelper object
    private DBHelper dbHelper;

    public static AddNewTodo newInstance(){
        return new AddNewTodo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_todo, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditText = view.findViewById(R.id.edtTodo);
        mSaveButton = view.findViewById(R.id.saveButton);

        dbHelper = new DBHelper(getActivity());

        // to check if user want to update or create new task
        boolean isUpdate = false;

        Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String todo = bundle.getString("todo");
            mEditText.setText(todo);

            if(todo.length() > 0){
                mSaveButton.setEnabled(false);
            }
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    mSaveButton.setEnabled(false);
                    mSaveButton.setBackgroundColor(R.color.primary);
                }else{
                    mSaveButton.setEnabled(true);
                    mSaveButton.setBackgroundColor(R.color.primary);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final boolean finalIsUpdate = isUpdate;
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();

                if(finalIsUpdate){
                    dbHelper.updateTodo(bundle.getInt("id"), text);
                } else{
                    Todo todoItem = new Todo();
                    todoItem.setTodoTitle(text);
                    todoItem.setStatus(0);
                    todoItem.setUser_id(1);
                    dbHelper.insertTodo(todoItem);
                }
                dismiss();
            }
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof OnDialogCloseListener){
            ((OnDialogCloseListener)activity).onDialogClose(dialog);
        }
    }
}
