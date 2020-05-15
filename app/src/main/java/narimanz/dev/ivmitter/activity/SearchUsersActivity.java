package narimanz.dev.ivmitter.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import narimanz.dev.ivmitter.R;
import narimanz.dev.ivmitter.adapter.UsersAdapter;
import narimanz.dev.ivmitter.network.HttpClient;
import narimanz.dev.ivmitter.pojo.User;

public class SearchUsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;
    private Toolbar toolbar;
    private EditText queryEditText;
    private Button searchButton;
    private HttpClient httpClient;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        initRecyclerView();

        toolbar = findViewById(R.id.toolbar);
        queryEditText = toolbar.findViewById(R.id.query_edit_text);
        searchButton = toolbar.findViewById(R.id.search_button);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchUsers();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchUsers();
            }
        });

        queryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchUsers();
                    return true;
                }
                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        httpClient = new HttpClient();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        usersRecyclerView = findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

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
        final String query = queryEditText.getText().toString();
        if(query.length() == 0) {
            Toast.makeText(SearchUsersActivity.this, R.string.not_enough_symbols_msg, Toast.LENGTH_SHORT).show();
            return;
        }
        new UsersAsyncTask().execute(query);
    }

    @SuppressLint("StaticFieldLeak")
    private class UsersAsyncTask extends AsyncTask<String, Integer, Collection<User>> {


        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Collection<User> doInBackground(String... params) {
            String query = params[0];
            try {
                return httpClient.readUsers(query);
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Collection<User> users) {
            swipeRefreshLayout.setRefreshing(false);
            if (users!=null){
                usersAdapter.clearItems();
                usersAdapter.setItems(users);
            }
            else {
                Toast.makeText(SearchUsersActivity.this,R.string.loading_error_msg,Toast.LENGTH_SHORT).show();
            }
        }
    }


}
