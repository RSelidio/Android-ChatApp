package com.example.itmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itmanagement.ChatActivity;
import com.example.itmanagement.models.ModelUser;
import com.example.itmanagement.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdepter extends RecyclerView.Adapter<UsersAdepter.UserHolder>{

    Context context;
    List<ModelUser> userList;


    public UsersAdepter(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent, false);

        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder userholder, int position) {

        String hisUID = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userfName = userList.get(position).getFirstName();
        String userlName = userList.get(position).getLastName();
        String useremail = userList.get(position).getEmail();

        userholder.mfirstNameTv.setText(userfName);
        userholder.mlastNameTv.setText(userlName);
        userholder.memailTv.setText(useremail);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_face)
                    .into(userholder.mprofileavatarTv);
        }
        catch (Exception e){

        }

        userholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUID", hisUID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return userList.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{


        ImageView mprofileavatarTv;
        TextView mfirstNameTv,mlastNameTv, memailTv;

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            mprofileavatarTv = itemView.findViewById(R.id.profileavatarTv);
            mfirstNameTv = itemView.findViewById(R.id.firstNameTv);
            mlastNameTv = itemView.findViewById(R.id.lastNameTv);
            memailTv = itemView.findViewById(R.id.emailTv);
        }
    }
}
