package tmp;

import di_ioc_engine.anotations.Autowired;
import di_ioc_engine.anotations.Component;
import di_ioc_engine.anotations.Qualifier;

@Component
@Qualifier("implUserA")
public class UserA implements User{
    @Override
    public void g() {
        subUser.g();
        System.out.println("printed from UserA");
    }

    @Autowired
    @Qualifier("implUserC")
    User subUser;
}
