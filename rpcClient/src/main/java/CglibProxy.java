import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by lenovo on 2016/12/23.
 */
public class CglibProxy {
    private Enhancer enhancer = new Enhancer();

    public Object getProxy(final Class clazz) {
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("start proxy");
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setArgs(objects);
                rpcRequest.setRequestId(UUID.randomUUID().toString());
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setInterfaceName(clazz.getName());
                RpcClient client = new RpcClient();
                client.connect();
                return client.send(rpcRequest);
            }
        });
        return enhancer.create();
    }


}
