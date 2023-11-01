package tmp;

import di_ioc_engine.anotations.Autowired;
import di_ioc_engine.anotations.Service;

@Service
public class ProkicClass {
    public void f()
    {
        aleksaClass.f();
        System.out.println("Printed from ProkicClass");
    }

    @Autowired
    AleksaClass aleksaClass;
}
