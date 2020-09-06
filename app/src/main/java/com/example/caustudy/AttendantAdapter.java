package com.example.caustudy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AttendantAdapter extends RecyclerView.Adapter<AttendantAdapter.ViewHolder> {

    private ArrayList<Item_attendant> listApplier = new ArrayList<>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendant, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Item을 하나씩 출력
        holder.onBind(listApplier.get(position));

    }

    @Override
    public int getItemCount() {
        // Item 수
        return listApplier.size();
    }

    public void addItem(Item_attendant item) {
        listApplier.add(item);
    }

    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onDelayClick(View v, int position);
        void onAttendClick(View v, int position) ;
        void onAbsentClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView email;
        private TextView dept;
        private ToggleButton _toggle1, _toggle2, _toggle3; //출석, 지각, 결석 순서

        ViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            email = itemView.findViewById(R.id.item_email);
            dept = itemView.findViewById(R.id.item_dept);
            _toggle1 = itemView.findViewById(R.id.toggle_1);
            _toggle2 = itemView.findViewById(R.id.toggle_2);
            _toggle3 = itemView.findViewById(R.id.toggle_3);

            _toggle1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_toggle1.isChecked()) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            // 리스너 객체의 메서드 호출.
                            if (mListener != null) {
                                mListener.onAttendClick(v, pos);
                            }
                        }
                    }
                }
            });
            _toggle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_toggle1.isChecked()) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            // 리스너 객체의 메서드 호출.
                            if (mListener != null) {
                                mListener.onDelayClick(v, pos);
                            }
                        }
                    }
                }
            });
            _toggle3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_toggle1.isChecked()) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            // 리스너 객체의 메서드 호출.
                            if (mListener != null) {
                                mListener.onAbsentClick(v, pos);
                            }
                        }
                    }
                }
            });
        }

        void onBind(Item_attendant item) {
            name.setText(item.getName());
            email.setText(item.getEmail());
            String dept_temp = item.getL_dept() + " " + item.getS_dept();
            dept.setText(dept_temp);
        }
    }
}

class Item_attendant {
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