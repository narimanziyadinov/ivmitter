package narimanz.dev.ivmitter.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Collection;

import narimanz.dev.ivmitter.R;
import narimanz.dev.ivmitter.adapter.UsersAdapter;
import narimanz.dev.ivmitter.pojo.User;

public class SearchUsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        initRecyclerView();

        searchUsers();
    }

    private void initRecyclerView() {
        usersRecyclerView = findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        UsersAdapter.OnUserClickListener onUserClickListener = new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Intent intent = new Intent(SearchUsersActivity.this, UserActivityInfo.class);
                intent.putExtra(UserActivityInfo.USER_ID, user.getId());
                startActivity(intent);
            }
        };
        usersAdapter = new UsersAdapter(onUserClickListener);
        usersRecyclerView.setAdapter(usersAdapter);
    }

    private void searchUsers() {
        Collection<User> users = getUsers();
        usersAdapter.setItems(users);
    }

    private Collection<User> getUsers() {
        return Arrays.asList(
                new User(
                        929257819349700608L,
                        "http://i.imgur.com/DvpvklR.png",
                        "Nariman Ziyadinov",
                        "@narimanz",
                        "Developer",
                        "Kazan",
                        42,
                        42
                ),

                new User(
                        44196397L,
                        "https://sun9-10.userapi.com/c855332/v855332245/14bd0f/G1N_bIeun4c.jpg",
                        "Aliya Safina",
                        "@aliya.s",
                        "Жена Наримана",
                        "Kazan",
                        14,
                        13
                )
        );
    }
}
