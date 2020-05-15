package narimanz.dev.ivmitter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import narimanz.dev.ivmitter.R;
import narimanz.dev.ivmitter.adapter.TweetAdapter;
import narimanz.dev.ivmitter.network.HttpClient;
import narimanz.dev.ivmitter.pojo.Tweet;
import narimanz.dev.ivmitter.pojo.User;

public class UserActivityInfo extends AppCompatActivity {
    private ImageView userImageView;
    private TextView nameTextView;
    private TextView nickTextView;
    private TextView descriptionTextView;
    private TextView locationTextView;
    private TextView followingCountTextView;
    private TextView followersCountTextView;

    private RecyclerView tweetsRecyclerView;
    private TweetAdapter tweetAdapter;
    private Toolbar toolbar;
    public static final String USER_ID = "userId";

    private HttpClient httpClient;

    private SwipeRefreshLayout swipeRefreshLayout;
    private int taskInProgressCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        final long userID = getIntent().getLongExtra(USER_ID, -1);
        Toast.makeText(this,"userID = " + userID,Toast.LENGTH_SHORT).show();

        userImageView = findViewById(R.id.user_image_view);
        nameTextView = findViewById(R.id.user_name_text_view);
        nickTextView = findViewById(R.id.user_nick_text_view);
        descriptionTextView = findViewById(R.id.user_description_text_view);
        locationTextView = findViewById(R.id.user_location_text_view);
        followingCountTextView = findViewById(R.id.following_count_text_view);
        followersCountTextView = findViewById(R.id.followers_count_text_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetAdapter.clearItems();
                loadUserInfo(userID);
                loadTweets(userID);
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRecyclerView();

        httpClient = new HttpClient();

        loadUserInfo(userID);
        loadTweets(userID);
    }

    private void loadTweets(long userId) {
        new TweetsAsyncTask().execute(userId);
    }

//    private Collection<Tweet> getTweets() {
//        return Arrays.asList(
//                new Tweet(getUser(), 1L, "Thu Dec 13 07:31:08 +0000 2017", "Очень длинное описание твита 1",
//                        4L, 4L, "https://www.w3schools.com/w3css/img_fjords.jpg"),
//
//                new Tweet(getUser(), 2L, "Thu Dec 12 07:31:08 +0000 2017", "Очень длинное описание твита 2",
//                        5L, 5L, "https://www.w3schools.com/w3images/lights.jpg"),
//
//                new Tweet(getUser(), 3L, "Thu Dec 11 07:31:08 +0000 2017", "Очень длинное описание твита 3",
//                        6L, 6L, "https://www.w3schools.com/css/img_mountains.jpg")
//        );
//    }


    private void setRefreshLayoutVisible(boolean visible){
        if (visible){
            taskInProgressCount++;
            if (taskInProgressCount == 1){
                swipeRefreshLayout.setRefreshing(true);
            }
        }
        else {
            taskInProgressCount--;
            if (taskInProgressCount==0) swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_menu, menu);
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.messenger :
                Intent intent = new Intent(getApplicationContext(), StartMessageActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_search:
                Intent intent1 = new Intent(this, SearchUsersActivity.class);
                startActivity(intent1);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

    private void initRecyclerView() {
        tweetsRecyclerView = findViewById(R.id.tweets_recycler_view);

        ViewCompat.setNestedScrollingEnabled(tweetsRecyclerView,false);
        tweetsRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        tweetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tweetAdapter = new TweetAdapter();
        tweetsRecyclerView.setAdapter(tweetAdapter);
    }

    private void loadUserInfo(final long userId) {
        // передаём userId в метод execute
        new UserInfoAsyncTask().execute(userId);
    }

    @SuppressLint("StaticFieldLeak")
    private class TweetsAsyncTask extends AsyncTask<Long, Integer, Collection<Tweet>> {

        @Override
        protected void onPreExecute() {
            setRefreshLayoutVisible(true);
        }

        protected Collection<Tweet> doInBackground(Long... ids) {
            try {
                // достаём userId, который передали в метод execute
                Long userId = ids[0];
                return httpClient.readTweets(userId);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Collection<Tweet> tweets) {
            setRefreshLayoutVisible(false);
            if (tweets!=null){
                tweetAdapter.setItems(tweets);
            }
            else {
                Toast.makeText(UserActivityInfo.this,R.string.loading_error_msg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UserInfoAsyncTask extends AsyncTask<Long, Integer, User> {

        @Override
        protected void onPreExecute() {
            setRefreshLayoutVisible(true);
        }

        protected User doInBackground(Long... ids) {
            try {
                Long userId = ids[0];
                return httpClient.readUserInfo(userId);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(User user) {

            setRefreshLayoutVisible(false);
            // при успешном ответе
            if (user!=null){
                displayUserInfo(user);
            }
            else{
                Toast.makeText(UserActivityInfo.this,R.string.loading_error_msg,Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void displayUserInfo(User user) {
        Picasso.get().load(user.getImageUrl()).into(userImageView);
        nameTextView.setText(user.getName());
        nickTextView.setText(user.getNick());
        descriptionTextView.setText(user.getDescription());
        locationTextView.setText(user.getLocation());

        String followingCount = String.valueOf(user.getFollowingCount());
        followingCountTextView.setText(followingCount);

        String followersCount = String.valueOf(user.getFollowersCount());
        followersCountTextView.setText(followersCount);
        getSupportActionBar().setTitle(user.getName());
    }


//    private User getUser() {
//        return new User(
//                1L,
//                "http://i.imgur.com/DvpvklR.png",
//                "Nariman Ziyadinov",
//                "narimanz",
//                "Kazan",
//                "Kazan",
//                42,
//                42
//        );
//    }
}
