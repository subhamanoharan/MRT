package com.example.mrt;

import com.example.mrt.models.POD;

interface RetryCallback {
    void retryUpload(POD pod);
}