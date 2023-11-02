package tmp;

import di_ioc_engine.anotations.Component;
import di_ioc_engine.anotations.Qualifier;

@Component
@Qualifier("implUserB")
public class UserB implements User{
    @Override
    public void g() {

    }
}
