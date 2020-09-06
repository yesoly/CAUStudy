package com.example.caustudy.MemberManagement;


import android.media.Rating;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatingHistoryViewAdapter extends RecyclerView.Adapter<RatingHistoryViewAdapter.ViewHolder> {

    private ArrayList<RatingHistoryViewItem> listItem = new ArrayList<>();
    private String study_key;
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");

    List<String> listKey = new ArrayList<>();
    List<String> listRate = new ArrayList<>();
    List<String> listFeedback = new ArrayList<>();
    List<String> listStudyName = new ArrayList<>();
    RatingHistoryViewAdapter myself = this;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rates, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Item을 하나씩 출력
        Log.d("onBindViewHolder called","");
        holder.onBind(listItem.get(position));

    }

    @Override
    public int getItemCount() {
        // ApplyViewItem 수
        return listItem.size();
    }

    public void addItem(RatingHistoryViewItem ratingHistoryViewItem) {
        listItem.add(ratingHistoryViewItem);
    }
    public void clearItem() {listItem.clear();}
    public void setStudyKey(String studyKey) {
        this.study_key = studyKey;
    }

    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onDeleteClick(View v, int position);
        void onAcceptClick(View v, int position) ;
        void onRatingInquiry(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void update_rates_view(String id_mem) {
        Log.d("update_rates_view","start");
        Log.d("name_mem",id_mem);

        userRef.child(id_mem).child("ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    listKey.clear();
                    listFeedback.clear();
                    listRate.clear();
                    listItem.clear();
                }
                Log.d("test1",dataSnapshot.getKey().toString());

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("ds.key :",ds.getKey().toString());
                    studyRef.child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String study_name = dataSnapshot.child("study_name").getValue().toString();
                            String study_key = ds.getKey().toString();
                            String rate = ds.child("rate").getValue().toString();
                            String feedback = ds.child("feedback").getValue().toString();
                            listKey.add(study_key);
                            listRate.add(rate);
                            listFeedback.add(feedback);
                            listStudyName.add(study_name);
                            Log.d("list added",study_key + rate + feedback + study_name);

                            RatingHistoryViewItem ratingViewItem = new RatingHistoryViewItem();
                            ratingViewItem.setFeedback(feedback);
                            ratingViewItem.setRate(rate);
                            ratingViewItem.setStudyName(study_name);
                            listItem.add(ratingViewItem);
                            myself.notifyDataSetChanged();
                            Log.d("this","");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });
                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
        this.notifyDataSetChanged();

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView study_name;
        private RatingBar rate;
        private TextView feedback;

        ViewHolder(final View itemView) {
            super(itemView);
            study_name = itemView.findViewById(R.id.study_name_view);
            rate = itemView.findViewById(R.id.rate_star_view);
            feedback = itemView.findViewById(R.id.feedback_view);
        }

        void onBind(RatingHistoryViewItem ratingViewItem) {
            study_name.setText(ratingViewItem.getStudyName());
            rate.setRating(Float.parseFloat(ratingViewItem.getRate()));
            feedback.setText(ratingViewItem.getFeedback());
        }
    }

}