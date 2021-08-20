package com.archieadams.server;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;


public class Server {

    public static String splitMarker = "/v/";

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(4999);
        while (true) {
            Socket socket = serverSocket.accept();

            System.out.println("Connected");

            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);


            while (true) {
                String methodName ="";
                try {
                    methodName = bufferedReader.readLine();
                }catch (SocketException e){
                    break;
                }
                int numberOfParameters = Integer.parseInt(bufferedReader.readLine());


                try {
                    Class aClass = Class.forName("com.archieadams.server.Server");
                    Class[] parameterTypes = new Class[numberOfParameters];
                    Object[] clientVariables = new Object[numberOfParameters];
                    for (int i = 0; i < numberOfParameters; i++) {
                        String clientMessage = bufferedReader.readLine();
                        ArrayList<String> clientMessageSplit = new ArrayList<>(Arrays.asList(clientMessage.split(splitMarker)));
                        int index;
                        if (clientMessageSplit.size() == 2) {
                            index = 1;
                        } else {
                            System.out.println("The user has entered there own split marker");
                            index = clientMessageSplit.size() - 1;
                        }

                        switch (clientMessageSplit.get(index)) {
                            case "str":
                                parameterTypes[i] = (String.class);
                                clientVariables[i] = clientMessageSplit.get(0);
                                break;

                            case "int":
                                parameterTypes[i] = (Integer.TYPE);
                                clientVariables[i] = Integer.parseInt(clientMessageSplit.get(0));
                                break;

                            case "bol":
                                parameterTypes[i] = (Boolean.class);
                                clientVariables[i] = Boolean.parseBoolean(clientMessageSplit.get(0));
                                break;

                            default:
                                break;
                        }

                        System.out.println("Variable added");

                    }

                    Method method = aClass.getMethod(methodName, parameterTypes);
                    Server obj = new Server();
                    Object returnObject = method.invoke(obj, clientVariables);
                    Integer returnValue = (Integer) returnObject;
                    System.out.println(returnValue.intValue());
                    respond(socket, returnValue.toString());
                } catch (Throwable e) {
                    System.err.println(e);
                }
            }
        }
    }

    public int add(int num1, int num2){
        return num1+num2;
    }

    public static void respond(Socket socket, String response) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(response);
        printWriter.flush();
    }
}
