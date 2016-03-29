package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.spi.APISignatureMethod;
import com.cheche365.cheche.signature.spi.PreSignRequest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


/**
 * Created by zhengwei on 5/20/15.
 * 签名／验签工具类
 */
public class APISignature {


    public static String generate(PreSignRequest request, Parameters params, Secrets secrets) throws SignatureException {
        return getSignatureMethod(params).sign(elements(request, params), secrets);
    }

    public static void sign(PreSignRequest request, Parameters params, Secrets secrets) throws SignatureException {
        params = (Parameters)params.clone(); //不修改原始请求数据
        params.setSignature(generate(request, params, secrets));
        params.writeRequest(request);
    }

    public static boolean verify(PreSignRequest request, Parameters params, Secrets secrets) throws SignatureException {
        return getSignatureMethod(params).verify(elements(request, params), secrets, params.getSignature());
    }

    static String normalizeParameters(PreSignRequest request, Parameters params) throws UnsupportedEncodingException {

        ArrayList<String[]> list = new ArrayList<String[]>();

        for (String key : params.keySet()) {

            if (key.equals(Parameters.SIGNATURE)) { //忽略签名header
                continue;
            }

            String value = params.get(key);

            if (value != null) {
                addParam(key, value, list);
            }
        }

        for (String key : request.getParameterNames()) {
            String value = request.getParameterValue(key);
            addParam(key, value, list);
        }

        Collections.sort(list, new Comparator<String[]>() {
            @Override
            public int compare(String[] t, String[] t1) {
                int c = t[0].compareTo(t1[0]);
                return c == 0 ? t[1].compareTo(t1[1]) : c;
            }
        });

        StringBuilder buf = new StringBuilder();

        for (Iterator<String[]> i = list.iterator(); i.hasNext(); ) {
            String[] param = i.next();
            buf.append(param[0]).append("=").append(param[1]);
            if (i.hasNext()) {
                buf.append('&');
            }
        }

        return buf.toString();
    }

    private static URI constructRequestURL(PreSignRequest request) throws SignatureException {
        try {
            URL url = request.getRequestURL();
            if (url == null)
                throw new SignatureException();
            StringBuffer buf = new StringBuffer(url.getProtocol()).append("://").append(url.getHost().toLowerCase());
            int port = url.getPort();
            if (port > 0 && port != url.getDefaultPort()) {
                buf.append(':').append(port);
            }
            buf.append(url.getPath());
            return new URI(buf.toString());

        } catch (URISyntaxException mue) {
            throw new SignatureException(mue);
        }
    }

    private static String elements(PreSignRequest request, Parameters params) throws SignatureException {

        StringBuilder buf = new StringBuilder(request.getRequestMethod().toUpperCase());

        try {
            buf.append('&').append(constructRequestURL(request).toASCIIString());
            buf.append('&').append(URLEncoder.encode(normalizeParameters(request, params), "UTF-8"));
            buf.append('&').append(URLEncoder.encode(String.valueOf(request.getEntity() == null ? "" : request.getEntity()), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("JVM不支持UTF-8");
        }

        return buf.toString();
    }

    private static APISignatureMethod getSignatureMethod(Parameters params) throws UnsupportedSignatureMethodException {
        APISignatureMethod method = MethodFactory.getInstance(params.getSignatureMethod());
        if (method == null) {
            throw new UnsupportedSignatureMethodException(params.getSignatureMethod());
        }
        return method;
    }

    private static void addParam(String key, String value, List<String[]> list) throws UnsupportedEncodingException {
        list.add(new String[] { URLEncoder.encode(key, "UTF-8"),
            value == null ? "" : URLEncoder.encode(value, "UTF-8")
        });
    }
}

