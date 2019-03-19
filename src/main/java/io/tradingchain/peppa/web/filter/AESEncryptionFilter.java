package io.tradingchain.peppa.web.filter;

import io.tradingchain.peppa.util.AESEncryptionUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StopWatch;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

class AESEncryptionFilter implements Filter {

    private String key;

    public AESEncryptionFilter(String key) {
        this.key = key;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if(servletRequest instanceof HttpServletRequest) {
            requestWrapper = new AESEncryptedHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        }
        ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);

        if(requestWrapper == null) {
            filterChain.doFilter(servletRequest, responseWrapper);
        } else {
            filterChain.doFilter(requestWrapper, responseWrapper);
        }

        byte[] responseData = responseWrapper.getResponseData();
        String encrypted = AESEncryptionUtil.encrypt(this.key, responseData);
        servletResponse.getWriter().println(encrypted);
    }

    class AESEncryptedHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private byte[] body;

        public AESEncryptedHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            byte[] temp = new byte[request.getContentLength()];
            IOUtils.read(request.getInputStream(), temp);
            body = AESEncryptionUtil.decrypt(key, temp);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body);

            return new ServletInputStream() {

                @Override
                public int read() throws IOException {
                    return bais.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }
            };
        }
    }

    class ResponseWrapper extends HttpServletResponseWrapper {

        private ByteArrayOutputStream buffer = null;
        private ServletOutputStream out = null;
        private PrintWriter writer = null;

        public ResponseWrapper(HttpServletResponse resp) throws IOException {
            super(resp);
            buffer = new ByteArrayOutputStream();// 真正存储数据的流
            out = new WapperedOutputStream(buffer);
            writer = new PrintWriter(new OutputStreamWriter(buffer));
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return out;
        }

        @Override
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            if (out != null) {
                out.flush();
            }
            if (writer != null) {
                writer.flush();
            }
        }

        @Override
        public void reset() {
            buffer.reset();
        }

        public byte[] getResponseData() throws IOException {
            flushBuffer();
            return buffer.toByteArray();
        }

        public String getContent() throws IOException {
            flushBuffer();
            return buffer.toString();
        }


        private class WapperedOutputStream extends ServletOutputStream {
            private ByteArrayOutputStream bos = null;

            public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
                bos = stream;
            }

            @Override
            public void write(int b) throws IOException {
                bos.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                bos.write(b, 0, b.length);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                bos.write(b, off, len);
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }
        }
    }
}