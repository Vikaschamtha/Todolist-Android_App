package com.example.todolist;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.model.TaskModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private ArrayList<TaskModel> taskDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskNameTv, taskStatusTv;
        LinearLayout containerLl;

        public ViewHolder(View view) {
            super(view);
            taskNameTv = view.findViewById(R.id.taskNameTv);
            taskStatusTv = view.findViewById(R.id.taskStatusTv);
            containerLl = view.findViewById(R.id.containerLL);
        }
    }

    public TaskListAdapter(ArrayList<TaskModel> taskDataset) {
        this.taskDataset = taskDataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TaskModel task = taskDataset.get(position);

        viewHolder.taskNameTv.setText(task.getTaskName());
        viewHolder.taskStatusTv.setText(task.getTaskStatus());

        String status = task.getTaskStatus().toLowerCase(Locale.ROOT);
        if (status.equals("pending")) {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FFFF00"));
        } else if (status.equals("completed")) {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#00FF00"));
        } else {
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        viewHolder.containerLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), viewHolder.containerLl);
                popupMenu.inflate(R.menu.taskmenu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        if (adapterPosition == RecyclerView.NO_POSITION) {
                            return false;
                        }

                        TaskModel task = taskDataset.get(adapterPosition);

                        if (menuItem.getItemId() == R.id.deleteMenu) {
                            FirebaseFirestore.getInstance().collection("tasks").document(task.getTaskId()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                            taskDataset.remove(adapterPosition);
                                            notifyItemRemoved(adapterPosition);
                                        }
                                    });
                        }

                        if (menuItem.getItemId() == R.id.markCompleteMenu) {
                            task.setTaskStatus("completed");

                            FirebaseFirestore.getInstance().collection("tasks").document(task.getTaskId())
                                    .set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext(), "Task Item Marked As Completed", Toast.LENGTH_SHORT).show();
                                            notifyItemChanged(adapterPosition);
                                        }
                                    });
                        }

                        return false;
                    }
                });

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskDataset.size();
    }

    public void clearAllItems() {
        taskDataset.clear();
        notifyDataSetChanged();
    }
}
