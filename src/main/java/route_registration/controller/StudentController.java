package route_registration.controller;

import di_ioc_engine.anotations.Autowired;
import route_registration.anotations.Controller;
import route_registration.anotations.GET;
import route_registration.anotations.POST;
import route_registration.anotations.Path;
import tmp.StudentClass;

@Controller
public class StudentController {

    @Autowired
    StudentClass studentClass;

    @GET
    @Path(path_url = "http://localhost:8080/initials")
    public String printStudentInitials(String name, String surname)
    {
        return studentClass.printStudentInitials(name,surname);
    }

    @GET
    @Path(path_url = "http://localhost:8080/fullName")
    public String printStudentFullName(String name, String surname) {return studentClass.printStudentFullName(name,surname);}
}
