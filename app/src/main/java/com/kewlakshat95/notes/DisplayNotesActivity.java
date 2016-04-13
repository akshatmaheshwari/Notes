package com.kewlakshat95.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DisplayNotesActivity extends AppCompatActivity {
    ListView lvNotes;
    ArrayList alTitles, alNotes;
    DBHelper db;
    ListViewAdapter listViewAdapter;
    List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_display_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DisplayNotesActivity.this, NewNoteActivity.class);
                startActivity(i);
            }
        });

        lvNotes = (ListView) findViewById(R.id.lvNotes);
        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putInt("id", (int)(db.numberOfRows() - position));
                Intent i = new Intent(DisplayNotesActivity.this, ShowNoteActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        db = new DBHelper(this);
        alTitles = db.getAllTitles();
        alNotes = db.getAllNotes();
        noteList = new ArrayList<Note>();
        for (int i = 0; i < db.numberOfRows(); i++) {
            Note note = new Note(alTitles.get(i).toString(), alNotes.get(i).toString());
            noteList.add(note);
        }
        listViewAdapter = new ListViewAdapter(this, R.layout.note_list_item, noteList);
        lvNotes.setAdapter(listViewAdapter);
        lvNotes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvNotes.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = lvNotes.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                listViewAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_display_notes, menu);
                getSupportActionBar().hide();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = listViewAdapter.getSelectedIds();
                        // Captures all selected ids with a loop
                        Note selectedItem = null;
                        for(int i = (selected.size() - 1); i >= 0; i--) {
                            if(selected.valueAt(i)) {
                                selectedItem = listViewAdapter.getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                listViewAdapter.remove(selectedItem);
                            }
                        }
                        int last = 0, n = db.numberOfRows();
                        for (int i = 0; i < n; i++) {
                            if (selected.get(i)) {
                                db.deleteNote(n - i);
                                last = i;
                            }
                        }
                        db.reconfigure((int)(n - last));
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listViewAdapter.removeSelection();
                getSupportActionBar().show();
            }
        });

        super.onResume();
    }
}
