package com.example.mrt;

import com.example.mrt.models.POD;

public interface RetryCallback {
    void retryUpload(POD pod);
}
