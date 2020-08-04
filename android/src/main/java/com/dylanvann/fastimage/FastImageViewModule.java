package com.dylanvann.fastimage;

import android.app.Activity;

import com.bumptech.glide.Glide;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.views.imagehelper.ImageSource;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

class FastImageViewModule extends ReactContextBaseJavaModule {

    private static final String REACT_CLASS = "FastImageView";
    private ReactApplicationContext mReactContext;

    FastImageViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void preload(final ReadableArray sources) {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < sources.size(); i++) {
                    final ReadableMap source = sources.getMap(i);
                    final FastImageSource imageSource = FastImageViewConverter.getImageSource(activity, source);

                    Glide
                            .with(activity.getApplicationContext())
                            // This will make this work for remote and local images. e.g.
                            //    - file:///
                            //    - content://
                            //    - res:/
                            //    - android.resource://
                            //    - data:image/png;base64
                            .load(
                                    imageSource.isBase64Resource() ? imageSource.getSource() :
                                    imageSource.isResource() ? imageSource.getUri() : imageSource.getGlideUrl()
                            )
                            .apply(FastImageViewConverter.getOptions(activity, imageSource, source))
                            .preload();
                }
            }
        });
    }
    
        private void clearImageCache(Activity activity) {
        activity.getApplicationContext().getCacheDir().delete();
        mReactContext.getCacheDir().delete();
    }

    @ReactMethod
    public void clearCache() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;

        ClearCacheTask clearCacheTask = new ClearCacheTask(activity.getApplicationContext(), mReactContext);
        clearCacheTask.execute();
        clearCacheTask.doInBackground();

        Glide.getPhotoCacheDir(activity.getApplicationContext()).delete();
        Glide.getPhotoCacheDir(mReactContext).delete();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.get(activity.getApplicationContext()).clearMemory();
                Glide.get(mReactContext).clearMemory();

                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();

                clearImageCache(activity);
            }
        });
    }
}
