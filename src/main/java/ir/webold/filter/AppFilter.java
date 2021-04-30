package ir.webold.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.*;

@Component
public class AppFilter extends OncePerRequestFilter {
    public static final String RRN="audit_rrn";
    public static final String START_TIME="audit_start_time";




    @Override
    public void doFilterInternal(HttpServletRequest  req, HttpServletResponse  res, FilterChain filterChain) throws IOException, ServletException {
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(req);
        MutableHttpServletResponse mutableResponse = new MutableHttpServletResponse(res);
        String rrn = req.getHeader(RRN);
        mutableRequest.putHeader(START_TIME,String.valueOf(System.currentTimeMillis()));
        if (rrn == null) {
            rrn = UUID.randomUUID().toString();
            mutableRequest.putHeader(RRN, rrn);
        }
        mutableResponse.putHeader(RRN, rrn);
        filterChain.doFilter(mutableRequest, mutableResponse);
    }

    //warpper class
    final static class MutableHttpServletRequest extends HttpServletRequestWrapper {
        // holds custom header and value mapping
        private final Map<String, String> customHeaders;

        public MutableHttpServletRequest(HttpServletRequest request){
            super(request);
            this.customHeaders = new HashMap<>();
        }

        public void putHeader(String name, String value){
            this.customHeaders.put(name, value);
        }

        public String getHeader(String name) {
            // check the custom headers first
            String headerValue = customHeaders.get(name);

            if (headerValue != null){
                return headerValue;
            }
            // else return from into the original wrapped object
            return ((HttpServletRequest) getRequest()).getHeader(name);
        }

        public Enumeration<String> getHeaderNames() {
            // create a set of the custom header names
            Set<String> set = new HashSet<String>(customHeaders.keySet());

            // now add the headers from the wrapped request object
            @SuppressWarnings("unchecked")
            Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
            while (e.hasMoreElements()) {
                // add the names of the request headers into the list
                String n = e.nextElement();
                set.add(n);
            }

            // create an enumeration from the set and return
            return Collections.enumeration(set);
        }
    }

    final static class MutableHttpServletResponse extends HttpServletResponseWrapper {
        // holds custom header and value mapping
        private final Map<String, String> customHeaders;

        public MutableHttpServletResponse(HttpServletResponse request){
            super(request);
            this.customHeaders = new HashMap<>();
        }

        public void putHeader(String name, String value){
            this.customHeaders.put(name, value);
        }

    }
}
