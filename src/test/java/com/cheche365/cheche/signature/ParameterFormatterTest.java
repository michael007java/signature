package com.cheche365.cheche.signature;

import com.cheche365.cheche.signature.spi.PreSignRequest;
import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by zhengwei on 5/16/15.
 * 格式化待签名参数单元测试类。根据对参数排序并生成统一格式的字符串。
 */
public class ParameterFormatterTest extends TestCase {

    public ParameterFormatterTest(String testName) {
        super(testName);
    }

    public void testNormalizeParameters() throws UnsupportedEncodingException {
        final HashMap<String, String> params = new HashMap();
        params.put("org-country", "US");
        params.put("a", "b");
        params.put("org", "dummy");

        String normalizedParams = APISignature.normalizeParameters(new PreSignRequest() {
            @Override
            public String getRequestMethod() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public URL getRequestURL() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Set<String> getParameterNames() {
                return params.keySet();
            }

            @Override
            public String getParameterValue(String name) {
                return params.get(name);
            }

            @Override
            public List<String> getHeaderValues(String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Object getEntity() {
                return "";
            }


            @Override
            public void addHeaderValue(String name, String value) throws IllegalStateException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, new Parameters());

        assertEquals("a=b&org=dummy&org-country=US", normalizedParams);
    }

    public void testNullParamValue() throws UnsupportedEncodingException {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("org-country", "US");
        params.put("org", null);
        params.put("a", "b");

        String normalizedParams = APISignature.normalizeParameters(new PreSignRequest() {
            @Override
            public String getRequestMethod() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public URL getRequestURL() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Set<String> getParameterNames() {
                return params.keySet();
            }

            @Override
            public String getParameterValue(String name) {
                return params.get(name) == null ? "" : params.get(name).toString();
            }


            @Override
            public List<String> getHeaderValues(String name) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Object getEntity() {
                return "{a:1,b:2}";
            }

            @Override
            public void addHeaderValue(String name, String value) throws IllegalStateException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, new Parameters());

        assertEquals("a=b&org=&org-country=US", normalizedParams);
    }
}
