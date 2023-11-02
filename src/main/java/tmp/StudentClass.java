package tmp;

import di_ioc_engine.anotations.Service;

@Service(scope = "prototype")
public class StudentClass {
    public void f() {System.out.println("Printed from StudentClass");}

    public String printStudentFullName(String name, String surname) {return name + " " + surname;}

    public String printStudentInitials(String name, String surname)
    {
        String a = name.length() > 0 ? String.valueOf(name.charAt(0)) : "-";
        String b = surname.length() > 0 ? String.valueOf(surname.charAt(0)) : "-";

        return a + ". " + b + ".\n";
    }


}
