package tmp;

import di_ioc_engine.anotations.Service;

@Service(scope = "prototype")
public class StudentClass {
    public void f() {System.out.println("Printed from StudentClass");}
}
