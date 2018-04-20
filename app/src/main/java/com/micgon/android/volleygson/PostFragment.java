package com.micgon.android.volleygson;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class PostFragment extends Fragment {
    private static final String ENDPOINT = "https://kylewbanks.com/rest/posts.json";
    private static final String TAG = "PostActivity";

    private RequestQueue requestQueue;
    private Gson gson;
    private PostMaster mPostMaster; // TODO Declare PostAdapter
    private Post mPost;
    private PostAdapter mPostAdapter;

    private RecyclerView mPostRecyclerView;
    private int mItemPosition;

    public static PostFragment newInstance() {
        return new PostFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handles running our request in a background thread.
        requestQueue = Volley.newRequestQueue(getContext());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        // This instance of Gson is what we'll be using to parse the JSON response within onPostLoaded
        gson = gsonBuilder.create();

        fetchPosts();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        mPostMaster = PostMaster.getInstance(getActivity());

        mPostRecyclerView = v.findViewById(R.id.post_recycler_view);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    private void fetchPosts() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostsLoaded, onPostsError);

        requestQueue.add(request);
    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            // Deserialize Json response into a array of posts objects -
            // then convert the array to List of post.
            List<Post> posts = Arrays.asList(gson.fromJson(response, Post[].class));
            Log.i(TAG, posts.size() + " posts loaded.");
            for (Post post : posts) {
                Log.i(TAG, post.getID() + ": " + post.getTitle());
                mPostMaster.addPost(post);
            }
            //Log.i(TAG, response);
            //postTitleText.setText(posts.get(0).getUrl());
        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.toString());
        }
    };

    private class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView postIdText;
        private TextView postTitleText;


        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_post, parent, false));
            itemView.setOnClickListener(this);
            postIdText = itemView.findViewById(R.id.post_id);
            postTitleText = itemView.findViewById(R.id.post_title);


        }

        private void bind(Post post) {
            mPost = post;

            postIdText.setText(String.valueOf(post.getID()));
            postTitleText.setText(post.getUrl());
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class PostAdapter extends RecyclerView.Adapter<PostHolder> {
        List<Post> mPosts;

        public PostAdapter(List<Post> posts) {
            mPosts = posts;
        }
        public void setPosts(List<Post> posts) {
            mPosts = posts;
        }

        @NonNull
        @Override
        public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PostHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PostHolder holder, int position) {
            Post post = mPosts.get(position);
            mItemPosition = position;
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }
    }

    public void updateUI() {
        List<Post> posts = mPostMaster.getPosts();
        if (mPostAdapter == null) {
            mPostAdapter = new PostAdapter(posts);
            mPostRecyclerView.setAdapter(mPostAdapter);
        } else {
            mPostAdapter.setPosts(posts);
            mPostAdapter.notifyItemChanged(mItemPosition);
        }
    }


}
