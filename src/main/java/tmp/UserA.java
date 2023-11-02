package tmp;

import di_ioc_engine.anotations.Component;
import di_ioc_engine.anotations.Qualifier;

@Component
@Qualifier("implUserA")
public class UserA implements User{
    @Override
    public void g() {
        System.out.println("printed from UserA");
    }
}
