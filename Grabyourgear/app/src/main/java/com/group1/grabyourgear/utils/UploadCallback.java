package com.group1.grabyourgear.utils;

public interface UploadCallback {
    void onSuccess(String imageUrl);
    void onError(Exception e);
}

