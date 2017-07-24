package com.diodev.speak.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diodev.speak.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<String> mGroups;
    private IGroup mIGroup;

    public GroupAdapter(List<String> groups, IGroup iGroup) {
        mGroups = groups;
        mIGroup = iGroup;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String groupName = mGroups.get(position);
        holder.setGroupName("{fa-circle-o} " + groupName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIGroup.onClick(groupName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mGroupName;

        public ViewHolder(View itemView) {
            super(itemView);
            mGroupName = (TextView) itemView.findViewById(R.id.tv_group_name);
        }

        public void setGroupName(String message) {
            if (null == mGroupName) return;
            mGroupName.setText(message);
        }
    }

    public interface IGroup {
        void onClick(String groupName);
    }
}
