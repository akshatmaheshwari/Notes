package com.kewlakshat95.notes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class NewNoteActivity extends AppCompatActivity {
    EditText etTitle, etNote;
    DBHelper db;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_new_note);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etNote = (EditText) findViewById(R.id.etNote);

        id = -1;

        db = new DBHelper(this);

        if(getIntent().hasExtra("id")) {
            Bundle b = getIntent().getExtras();
            id = b.getInt("id");
            Cursor c = db.getData(id);
            c.moveToFirst();
            etTitle.setText(c.getString(c.getColumnIndex(DBHelper.NOTES_COLUMN_TITLE)));
            etNote.setText(c.getString(c.getColumnIndex(DBHelper.NOTES_COLUMN_NOTE)));
        }
    }

    @Override
    protected void onPause() {
        String title = etTitle.getText().toString(), note = etNote.getText().toString();
        if(getIntent().hasExtra("id")) {
            db.updateNote(getIntent().getExtras().getInt("id"), title, note);
        } else {
            if(id == -1) {
                if (!title.equals("") || !note.equals("")) {
                    id = db.insertNote(title, note);
                }
            } else {
                db.updateNote(id, title, note);
            }
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
