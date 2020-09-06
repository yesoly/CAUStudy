package com.example.caustudy.MemberManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.R;

import java.util.ArrayList;

public class BlockMemberViewAdapter extends RecyclerView.Adapter<BlockMemberViewAdapter.ViewHolder> {

    private ArrayList<BlockMemberViewItem> listMember = new ArrayList<>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block_member, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Item을 하나씩 출력
        holder.onBind(listMember.get(position));

    }

    @Override
    public int getItemCount() {
        // MemberViewItem 수
        return listMember.size();
    }

    public void addItem(BlockMemberViewItem memberViewItem) {
        listMember.add(memberViewItem);
    }
    public void clearItem() { listMember.clear();}

    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onUnblockClick(View v, int position) ;
        void onKickClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView email;
        private TextView dept;
        private Button unblock;
        private Button kick;
        private String study_key;

        ViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            email = itemView.findViewById(R.id.item_email);
            dept = itemView.findViewById(R.id.item_dept);
            unblock = itemView.findViewById(R.id.btn_unblock);

            unblock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onUnblockClick(v, pos);
                            listMember.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    }

                }
            });


        }

        void onBind(BlockMemberViewItem memberViewItem) {
            name.setText(memberViewItem.getName());
            email.setText(memberViewItem.getEmail());
            String dept_temp = memberViewItem.getL_dept() + " " + memberViewItem.getS_dept();
            dept.setText(dept_temp);
        }
    }
}

class BlockMemberViewItem {
    private String name, email, l_dept, s_dept;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getL_dept() {
        return l_dept;
    }
    public void setL_dept(String l_dept) {
        this.l_dept = l_dept;
    }

    public String getS_dept() {
        return s_dept;
    }
    public void setS_dept(String s_dept) {
        this.s_dept = s_dept;
    }
}