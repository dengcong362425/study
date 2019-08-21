package cn.sunline.crcs.service.impl;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class RPCProvidersContrller implements ApplicationContextAware{

	private ApplicationContext applicationContext;
	@RequestMapping({"/**"})
	String hello(@RequestBody String req,HttpServletRequest request) throws Exception {
		String requestURI = request.getRequestURI();
		String[] split = requestURI.split("/");
		Object bean = applicationContext.getBean(split[split.length-2]);
		Method method = bean.getClass().getMethod(split[split.length-1], String.class);
		return method.invoke(bean, req).toString();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
