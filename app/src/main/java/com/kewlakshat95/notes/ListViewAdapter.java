package com.kewlakshat95.notes;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Note> {
    Context context;
    LayoutInflater inflater;
    List<Note> noteList;
    private SparseBooleanArray mSelectedItemsIds;

    public ListViewAdapter(Context context, int resource, List<Note> noteList) {
        super(context, resource, noteList);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.noteList = noteList;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView title;
        TextView note;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.note_list_item, null);
            // Locate the TextViews in listview_item.xml
            holder.title = (TextView) view.findViewById(R.id.tvTitleList);
            holder.note = (TextView) view.findViewById(R.id.tvNoteList);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.title.setText(noteList.get(position).getTitle());
        holder.note.setText(noteList.get(position).getNote());
        return view;
    }

    @Override
    public void remove(Note object) {
        noteList.remove(object);
        notifyDataSetChanged();
    }

    public List<Note> getWorldPopulation() {
        return noteList;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
