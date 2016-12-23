import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by lenovo on 2016/12/23.
 */
public class CglibProxy implements MethodInterceptor {
    private Enhancer enhancer=new Enhancer();
    public Object getProxy(Class clazz){
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("start proxy");
        RpcResponse rpcResponse=new RpcResponse();
        try {
            Object result=method.invoke(o,objects);

        }catch (Exception e){
            rpcResponse.setException(e);
        }

        System.out.println("end proxy");
        return rpcResponse;
    }
}
