package se.callista.microservices.util;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * Created by magnus on 01/05/15.
 */
public interface LambdaServletFilter extends Filter {

    default public void init(FilterConfig filterConfig) throws ServletException {}
    default public void destroy() {}
}
