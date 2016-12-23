package serviceInterfaceImpl;

import serviceInterface.HelloService;
import Anotation.Service;


/**
 * Created by lenovo on 2016/12/23.
 */
@Service(value="HelloService")
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(String name) {
        return "hello "+name;
    }
}
