package com.example.itmanagement.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itmanagement.R;
import com.example.itmanagement.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder>{

    Context context;
    List<ModelPost> postList;



    public AdapterPost(Context context, List<ModelPost> postList) {
        this.postList = postList;
        this.context = context;
    }



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String udi = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String firstName = postList.get(position).getfirstName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescr = postList.get(position).getpDesrc();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy  hh:mm aa", calendar). toString();

        holder.firstName.setText(firstName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDesrc.setText(pDescr);


        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_profile_white).into(holder.uPictureIv);
        }
        catch (Exception e){

        }
        if (pImage.equals("noImage")){
        holder.pImageIv.setVisibility(View.GONE);
        }
        else {
            try {
                Picasso.get().load(pImage).into(holder.pImageIv);

            }
            catch (Exception e){

            }

        }


        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
        }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView uPictureIv, pImageIv;
        TextView firstName, pTimeTv, pTitleTv, pDesrc, pLikesTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            firstName = itemView.findViewById(R.id.firstName);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDesrc = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);


        }
    }
}
