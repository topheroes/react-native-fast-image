package com.dylanvann.fastimage;

import android.content.Context;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.facebook.react.bridge.ReactApplicationContext;

class ClearCacheTask extends AsyncTask<Void, Void, Void> {
    Context mContext;
    ReactApplicationContext mReactContext;

    public ClearCacheTask(Context mContext, ReactApplicationContext mReactContext) {
        this.mContext = mContext;
        this.mReactContext = mReactContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Glide.get(mContext).clearDiskCache();
        Glide.get(mReactContext).clearDiskCache();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}