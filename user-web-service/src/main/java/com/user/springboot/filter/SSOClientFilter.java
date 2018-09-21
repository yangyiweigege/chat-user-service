package com.user.springboot.filter;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

//@WebFilter(filterName = "sSOClientFilter", urlPatterns = "/*")
public class SSOClientFilter implements Filter {

	@Autowired
	private RestTemplate restTemplate;
	private final static Logger logger = Logger.getLogger(SSOClientFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username"); // 从session中获取用户名
		String ticket = request.getParameter("ticket");
		String url = URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
		logger.info("执行客户端sso拦截.............");
		// 判断用户是否已经登录oa系统
		if (username == null) {
			// 1.判断用户是否有认证凭据--ticket（认证中心生成）
			if (ticket != null && !"".equals(ticket)) {
				username = restTemplate.getForObject("http://localhost:8080/sso-server/ticket?ticket=" + ticket,
						String.class);
				// 2.判断认证凭据是否有效
				if (!username.equals("illeageUser")) {
					// session设置用户名，说明用户登录成功了
					session.setAttribute("username", username);
					filterChain.doFilter(request, response);
				} else {
					response.sendRedirect("http://localhost:8080/sso-server/index.jsp?service=" + url);
				}

			} else {// 第一次访问oa系统，需要到sso-server系统验证
				response.sendRedirect("http://localhost:8080/sso-server/index.jsp?service=" + url); // 返回到oa系统
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
