package tmp;

import di_ioc_engine.anotations.Component;
import di_ioc_engine.anotations.Qualifier;

@Component
@Qualifier("implUserC")
public class UserC implements User{
    @Override
    public void g() {
        System.out.println("Printed from UserC");
    }
}
