package tmp;

import di_ioc_engine.anotations.Autowired;
import di_ioc_engine.anotations.Qualifier;
import di_ioc_engine.anotations.Service;

@Service
public class ProkicClass {
    public void f()
    {
        aleksaClass.f();
        user.g();
        System.out.println("Printed from ProkicClass");
    }

    @Autowired
    AleksaClass aleksaClass;

    @Autowired
    @Qualifier("implUserA")
    User user;
}
