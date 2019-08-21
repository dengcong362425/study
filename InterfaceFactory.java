package cn.sunline.crcs.service.temp;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

public class InterfaceFactory<T> implements FactoryBean<T> {
	
    private Class<T> interfaceType;
 
    public InterfaceFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }
 
    @SuppressWarnings("unchecked")
	@Override
    public T getObject() throws Exception {
        //这里主要是创建接口对应的实例，便于注入到spring容器中
    	InterfaceProxy<T> handler = new InterfaceProxy<>(this.interfaceType);
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[] {interfaceType},handler);
    }
 
    @Override
    public Class<T> getObjectType() {
        return interfaceType;
    }
 
    @Override
    public boolean isSingleton() {
        return true;
    }
}
