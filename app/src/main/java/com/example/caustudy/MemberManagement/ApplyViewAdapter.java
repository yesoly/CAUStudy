package com.example.caustudy.MemberManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ApplyViewAdapter extends RecyclerView.Adapter<ApplyViewAdapter.ViewHolder> {

    private ArrayList<ApplyViewItem> listApplier = new ArrayList<>();
    private String study_key;
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apply, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Item을 하나씩 출력
        holder.onBind(listApplier.get(position));

    }

    @Override
    public int getItemCount() {
        // ApplyViewItem 수
        return listApplier.size();
    }

    public void addItem(ApplyViewItem applyViewItem) {
        listApplier.add(applyViewItem);
    }
    public void clearItem() {listApplier.clear();}
    public void setStudyKey(String studyKey) {
        this.study_key = studyKey;
    }

    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onDeleteClick(View v, int position);
        void onAcceptClick(View v, int position) ;
        void onRatingInquiry(View v, int position);
        void onKickClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView email;
        private TextView dept;
        private ImageView accept;
        private ImageView delete;
        private Button btn_rating_view;
        private Button btn_block;

        ViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            email = itemView.findViewById(R.id.item_email);
            dept = itemView.findViewById(R.id.item_dept);
            accept = itemView.findViewById(R.id.apply_accept);
            delete = itemView.findViewById(R.id.apply_delete);
            btn_rating_view = itemView.findViewById(R.id.btn_rating_inquiry);
            btn_block = itemView.findViewById(R.id.btn_block);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onAcceptClick(v, pos);
                            if (listApplier.size() != 0) {
                                listApplier.remove(pos);
                                notifyDataSetChanged();

                            }

                        }
                    }
                }
            });
            btn_block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onKickClick(v, pos);
                            if (listApplier.size() != 0) {
                                listApplier.remove(pos);
                                //notifyItemRemoved(pos);
                                notifyDataSetChanged();

                            }
                        }
                    }
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onDeleteClick(v, pos) ;
                            if (listApplier.size() != 0) {
                                listApplier.remove(pos);
                                notifyDataSetChanged();

                                //notifyItemRemoved(pos);
                            }
                        }
                    }
                }
            });
            btn_rating_view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onRatingInquiry(v, pos);
                        }
                    }

                }
            });
        }

        void onBind(ApplyViewItem applyViewItem) {
            name.setText(applyViewItem.getName());
            email.setText(applyViewItem.getEmail());
            String dept_temp = applyViewItem.getL_dept() + " " + applyViewItem.getS_dept();
            dept.setText(dept_temp);
        }
    }
}

