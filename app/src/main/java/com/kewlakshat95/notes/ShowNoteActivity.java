package com.kewlakshat95.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowNoteActivity extends AppCompatActivity {
    TextView tvTitle, tvNote, tvEditedAt;
    DBHelper db;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_show_note);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNote = (TextView) findViewById(R.id.tvNote);
        tvEditedAt = (TextView) findViewById(R.id.tvEditedAt);

        db = new DBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_edit) {
            Bundle b1 = new Bundle();
            b1.putInt("id", b.getInt("id"));
            Intent i = new Intent(ShowNoteActivity.this, NewNoteActivity.class);
            i.putExtras(b);
            startActivity(i);
            return true;
        } else if(id == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setMessage("Delete note?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteNote(getIntent().getExtras().getInt("id"));
                            db.reconfigure(getIntent().getExtras().getInt("id"));
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
            return true;
        } else if(id == R.id.action_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, tvTitle.getText().toString());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, tvTitle.getText().toString() + "\n" + tvNote.getText().toString());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    protected void onResume() {
        b = getIntent().getExtras();
        int id = b.getInt("id");
        Cursor c = db.getData(id);
        c.moveToFirst();
        tvTitle.setText(c.getString(c.getColumnIndex(DBHelper.NOTES_COLUMN_TITLE)));
        tvNote.setText(c.getString(c.getColumnIndex(DBHelper.NOTES_COLUMN_NOTE)));
        tvEditedAt.setText(c.getString(c.getColumnIndex(DBHelper.NOTES_COLUMN_EDITED_AT)));
        super.onResume();
    }
}
