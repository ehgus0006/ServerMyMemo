package com.codesample.mymemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.codesample.mymemo.data.Memo;
import com.codesample.mymemo.data.User;
import com.codesample.mymemo.databinding.ActivityListBinding;
import com.codesample.mymemo.server.Server;
import com.codesample.mymemo.ui.MemoAdapter;
import com.codesample.mymemo.ui.Util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding binding;
    private Call<List<Memo>> getRequest;
    private Call<Void> deleteRequest;

    private MemoAdapter adapter;

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            deleteMemo(adapter.getItem(position));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
        User user = new User();
        user.name = preferences.getString("name", "");
        user.userid = preferences.getString("userid", "");

        binding.buttonUserInfo.setText(user.name);
        binding.buttonUserInfo.setOnClickListener(v -> Util.requestDialog(user, this));
        binding.buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, MemoActivity.class)));
        adapter = new MemoAdapter((int i, Memo memo)->{
            Intent intent = new Intent(this, MemoActivity.class);
            intent.putExtra("memoid", memo.memoid);
            startActivity(intent);
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(manager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
    }

    protected void onStart() {
        super.onStart();
        getList();
    }

    private void getList() {
        getRequest = Server.getInstance(this).getApi().getMemos();
        getRequest.enqueue(new Callback<List<Memo>>() {
            @Override
            public void onResponse(Call<List<Memo>> call, Response<List<Memo>> response) {
                if (response.code() == 200 && response.body() != null) {
                    adapter.updateDate(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Memo>> call, Throwable t) {

            }
        });
    }

    private void deleteMemo(Memo memo) {
        if (memo == null) return;
        deleteRequest = Server.getInstance(this).getApi().deleteMemo(memo.memoid);
        deleteRequest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200) getList();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getRequest!=null) getRequest.cancel();
        if(deleteRequest!=null) deleteRequest.cancel();
    }
}