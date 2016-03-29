package com.cheche365.cheche.signature;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by zhaozhong on 2016/1/11.
 */
public class BodyReadRequestWrapper extends HttpServletRequestWrapper {

    private byte[] myBytes;

    public BodyReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        myBytes = readInputStream(request.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            private int lastIndexRetrieved = -1;
            private ReadListener readListener = null;

            @Override
            public boolean isFinished() {
                return (lastIndexRetrieved == myBytes.length - 1);
            }

            @Override
            public boolean isReady() {
                return isFinished();
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                this.readListener = readListener;
                if (!isFinished()) {
                    try {
                        readListener.onDataAvailable();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                } else {
                    try {
                        readListener.onAllDataRead();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                }
            }

            @Override
            public int read() throws IOException {
                int i;
                if (!isFinished()) {
                    i = myBytes[lastIndexRetrieved + 1];
                    lastIndexRetrieved++;
                    if (isFinished() && (readListener != null)) {
                        try {
                            readListener.onAllDataRead();
                        } catch (IOException ex) {
                            readListener.onError(ex);
                            throw ex;
                        }
                    }
                    return i;
                } else {
                    return -1;
                }
            }
        };
    }

    byte[] readInputStream(InputStream inputStream) {
        ByteArrayOutputStream baos = null;
        BufferedInputStream bis = null ;
        try {
            baos = new ByteArrayOutputStream();
            bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int len ;
            while((len = bis.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
                baos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != baos) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return baos.toByteArray();
    }

}
