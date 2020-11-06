package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.spi.APISignatureMethod;
import com.cheche365.cheche.signature.spi.PreSignRequest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.cheche365.cheche.signature.Parameters.SIGNATURE;
import static com.cheche365.cheche.signature.Parameters.Version.VERSION_1_0;
import static com.cheche365.cheche.signature.Parameters.Version.getByVersionNo;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;



/***************************************************************************
 * APISignature.java
 * 文   件 名: APISignature.java
 * 模  块： 车险ToB平台
 * 功  能:  车车api签名工具
 * 初始创建:2015/5/20
 * 版本更新:V1.0
 * 版权所有:北京车与车科技有限公司
 ***************************************************************************/
public class APISignature {

    private static final Logger logger = Logger.getLogger(APISignature.class.getName());

    public static String generate(PreSignRequest request, Parameters params, Secrets secrets) throws SignatureException {
        return getSignatureMethod(params).sign(elements(request, params), secrets);
    }

    public static void sign(PreSignRequest request, Parameters params, Secrets secrets) throws SignatureException {
        // 不修改原始请求数据
        params = (Parameters) params.clone();
        params.setSignature(generate(request, params, secrets));
        params.writeRequest(request);
    }

    public static boolean verify(PreSignRequest request, Parameters params, Secrets secrets) throws Exception {
        return getSignatureMethod(params).verify(elements(request, params), secrets, params.getSignature());
    }

    static String normalizeParameters(PreSignRequest request, Parameters params) throws UnsupportedEncodingException {
        String signParamText = Stream.concat(
            // params
            params.entrySet().stream().filter(
                e -> !e.getKey().equals(SIGNATURE) && e.getValue() != null
            ).map(e -> new String[]{e.getKey(), e.getValue()}),
            // request's params
            request.getParameterNames().stream().map(k -> new String[]{k, request.getParameterValue(k)})
        ).sorted(comparing((String[] p) -> p[0]).thenComparing(p -> p[1])).map(p -> p[0] + "=" + p[1]).collect(joining("&"));

        logger.info("sign param string : " + signParamText);
        return signParamText;
    }

    private static URI constructRequestUri(PreSignRequest request, Parameters.Version version) throws SignatureException {
        try {
            URL url = request.getRequestURL();
            if (url == null) {
                throw new SignatureException("请求不包含URL");
            }
            StringBuilder buf = new StringBuilder();
            if (VERSION_1_0 == version) {
                buf.append(url.getProtocol()).append("://");
            }
            buf.append(url.getHost().toLowerCase());
            int port = url.getPort();
            if (port > 0 && port != url.getDefaultPort()) {
                buf.append(':').append(port);
            }
            buf.append(url.getPath());
            return new URI(buf.toString());

        } catch (URISyntaxException mue) {
            throw new SignatureException("构造请求URL失败", mue);
        }
    }

    private static String elements(PreSignRequest request, Parameters params) throws SignatureException {

        StringBuilder buf = new StringBuilder(request.getRequestMethod().toUpperCase());

        try {
            logger.info("request body : " + request.getEntity());

            buf.append('&').append(constructRequestUri(request,getByVersionNo(params.getVersion())).toASCIIString());
            buf.append('&').append(URLEncoder.encode(normalizeParameters(request, params), "UTF-8"));

            logger.info("request body is null : " + (request.getEntity() == null));

            String entityText = request.getEntityText();
            boolean notEncoded = entityText.equals(URLDecoder.decode(entityText.replace("%","%25"), "UTF-8")) || entityText.length() == URLDecoder.decode(entityText.replace("%","%25"), "UTF-8").length();
            if (notEncoded) {
                buf.append('&').append(URLEncoder.encode(entityText, "UTF-8"));
            } else {
                buf.append('&').append(entityText);
            }

        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("JVM不支持UTF-8");
        }

        logger.info("pre sign string : " + buf.toString());
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
        list.add(new String[]{URLEncoder.encode(key, "UTF-8"),
            value == null ? "" : URLEncoder.encode(value, "UTF-8")
        });
    }
}

