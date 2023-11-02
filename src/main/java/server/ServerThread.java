package server;

import framework.request.Header;
import framework.request.Helper;
import framework.request.Request;
import framework.request.enums.Method;
import framework.request.exceptions.RequestNotValidException;
import framework.response.JsonResponse;
import framework.response.Response;
import route_registration.RoutesRegistry;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String base_route = "http://localhost:8080";
    public RoutesRegistry routesRegistry = RoutesRegistry.getInstance();

    public ServerThread(Socket socket){
        this.socket = socket;

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {

            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }

            System.out.println("DEBUG");

            String path = request.getMethod().toString() +":"+base_route + request.getRoute();
            System.out.println("Path: " + path);
            java.lang.reflect.Method methodToInvoke = routesRegistry.routesRegistryMap.get(path);
            System.out.println("Method: " + methodToInvoke.getName());
            Object methodClassInstance = routesRegistry.methodClassMap.get(path);
            Object[] arguments = request.getParametersAsStringArray();
            System.out.println("Args: " + arguments.toString());
            String result = "-";
            try { result = (String) methodToInvoke.invoke(methodClassInstance,arguments);}
            catch (Exception e) {e.printStackTrace();}

            // Response example
            Map<String, Object> responseMap = new HashMap<>();
//            responseMap.put("route_location", request.getLocation());
//            responseMap.put("route_method", request.getMethod().toString());
//            responseMap.put("parameters", request.getParameters());
            responseMap.put("initials", result);
            Response response = new JsonResponse(responseMap);

            out.println(response.render());

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException e) {
            e.printStackTrace();
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        Method method = Method.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(Method.POST)) {
            int contentLength = Integer.parseInt(header.get("content-length"));
            char[] buff = new char[contentLength];
            in.read(buff, 0, contentLength);
            String parametersString = new String(buff);

            HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
            for (String parameterName : postParameters.keySet()) {
                parameters.put(parameterName, postParameters.get(parameterName));
            }
        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
