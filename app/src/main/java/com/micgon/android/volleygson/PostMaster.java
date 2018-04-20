package com.micgon.android.volleygson;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class PostMaster {
    private static PostMaster sPostMaster;
    private Context mContext;
    private List<Post> mPosts;

    public static PostMaster getInstance(Context context) {
        if (sPostMaster == null) {
            sPostMaster = new PostMaster(context);
        }
        return sPostMaster;
    }

    private PostMaster(Context context) {
        mContext = context.getApplicationContext();
        mPosts = new ArrayList<>();
    }

    public List<Post> getPosts() {

        return mPosts;
    }

    public void addPost(Post p) {
        mPosts.add(p);
    }
}
