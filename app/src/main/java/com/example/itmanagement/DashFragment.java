package com.example.itmanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.example.itmanagement.adapters.AdapterPost;
import com.example.itmanagement.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashFragment extends Fragment {


    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.postRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadPost();

        return view;

    }

    private void loadPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot datasnap : snapshot.getChildren()){

                    ModelPost modelPost = datasnap.getValue(ModelPost.class);

                    postList.add(modelPost);

                    adapterPost = new AdapterPost(getActivity(), postList);
                    recyclerView.setAdapter(adapterPost);

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(String searchQuery){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot datasnap : snapshot.getChildren()){

                    ModelPost modelPost = datasnap.getValue(ModelPost.class);

                    if (modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) || modelPost.getpDesrc().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(modelPost);
                    }
                    postList.add(modelPost);

                    adapterPost = new AdapterPost(getActivity(), postList);
                    recyclerView.setAdapter(adapterPost);

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){

        }else {

            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchPosts(query);
                }
                else {
                    loadPost();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchPosts(newText);
                }
                else {
                    loadPost();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_add_Post){
            startActivity(new Intent(getActivity(),AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}