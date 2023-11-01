package tmp;

import di_ioc_engine.anotations.MyBean;

@MyBean(scope = "prototype")
public class DunjaClass {
    public void f() { System.out.println("Printed from DunjaClass"); }
}
