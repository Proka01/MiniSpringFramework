package tmp;

import di_ioc_engine.anotations.Autowired;
import di_ioc_engine.anotations.Component;
import di_ioc_engine.anotations.Qualifier;

@Component
public class AleksaClass {
    public void f()
    {
        dunjaClass.f();
        System.out.println("Printed from AleksaClass");
    }

    @Autowired
    DunjaClass dunjaClass;

    @Autowired
    @Qualifier("implUserA")
    User user;
}
