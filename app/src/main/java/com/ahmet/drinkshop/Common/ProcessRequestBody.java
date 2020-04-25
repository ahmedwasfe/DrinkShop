package com.ahmet.drinkshop.Common;

import android.os.Handler;
import android.os.Looper;

import com.ahmet.drinkshop.Interface.UploadCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.reactivex.annotations.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProcessRequestBody extends RequestBody {

    private File file;
    private UploadCallBack uploadCallBack;
    private static final int DEFAULT_BUFFER_SIZE = 1408;

    public ProcessRequestBody(File file, UploadCallBack uploadCallBack) {
        this.file = file;
        this.uploadCallBack = uploadCallBack;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse("Image/*");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream fis = new FileInputStream(file);
        long uploaded = 0;
        try{
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = fis.read(buffer)) != -1){
                handler.post(new ProgressUpdater(uploaded, fileLength));
                uploaded+=read;
                sink.write(buffer,0,read);
            }
        }finally {
            fis.close();
        }
    }

    private class ProgressUpdater implements Runnable{

        private long uploaded, fileLength;
        public ProgressUpdater(long uploaded, long fileLength){
            this.uploaded = uploaded;
            this.fileLength = fileLength;
        }

        @Override
        public void run() {
            uploadCallBack.progressUpdate((int) (100*uploaded/fileLength));
        }
    }
}
