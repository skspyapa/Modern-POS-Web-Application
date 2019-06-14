package lk.ijse.dep.servlet;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(urlPatterns = "/customers/*")
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();

        JsonArrayBuilder ab = Json.createArrayBuilder();
        JsonObjectBuilder ob = Json.createObjectBuilder();

        String pathInfo = req.getPathInfo();


        Connection connection=null;
        if(pathInfo==null){

            try {

                BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
                connection = bds.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT * from customer");

                ResultSet resultSet = stmt.executeQuery();

                if(resultSet.isBeforeFirst()) {

                    while (resultSet.next()) {

                        String id = resultSet.getString(1);
                        String name = resultSet.getString(2);
                        String address = resultSet.getString(3);
                        //double salary = resultSet.getDouble(4);

                        ob.add("id", id);
                        ob.add("name", name);
                        ob.add("address", address);
                        //ob.add("salary", salary);
                        ab.add(ob.build());
                    }

                    JsonArray customersArray = ab.build();
                    writer.println(customersArray.toString());

                }else{
                    resp.sendError(404);
                }

            }catch (SQLException e) {

                resp.sendError(500);

                e.printStackTrace();
            }finally {
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
            }

        }else{

            try {

                BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
                connection = bds.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT * from customer where id=?");

                stmt.setObject(1,pathInfo.substring(1));

                ResultSet resultSet = stmt.executeQuery();

                if(resultSet.next()) {

                    String id = resultSet.getString(1);
                    String name = resultSet.getString(2);
                    String address = resultSet.getString(3);
                    //double salary = resultSet.getDouble(4);

                    ob.add("id", id);
                    ob.add("name", name);
                    ob.add("address", address);
                    //ob.add("salary", salary);

                    JsonObject customersArray = ob.build();
                    writer.println(customersArray.toString());

                }else {

                    resp.sendError(400);
                }

            } catch (SQLException e) {

                resp.sendError(500);

                e.printStackTrace();

            }finally {
                if(connection!=null){
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
Connection connection=null;
        try {

            ServletInputStream is = req.getInputStream();
            JsonReader reader = Json.createReader(is);
            JsonObject customer = reader.readObject();

            String id = customer.getString("id");
            String name = customer.getString("name");
            String address = customer.getString("address");
            //double salary = customer.getJsonNumber("salary").doubleValue();

            if (id == null || name == null || address == null) {

                resp.sendError(400);

            } else {
                BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
                connection = bds.getConnection();
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO customer set id=?,name=?,address=?");

                stmt.setObject(1,id);
                stmt.setObject(2,name);
                stmt.setObject(3,address);
                //stmt.setObject(4,salary);

                boolean result = stmt.executeUpdate() > 0;

                    if (result){

                        //resp.sendError(200);
                        writer.println("true");
                    }else {
                        writer.println("false");
                        //resp.sendError(400);

                    }
            }

        }catch (SQLException e) {

            resp.sendError(500);

            e.printStackTrace();

        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
Connection connection=null;
        try{
            BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
            connection = bds.getConnection();
            PreparedStatement stmt = connection.prepareStatement("DELETE from customer where id=?");

            stmt.setObject(1,pathInfo.substring(1));

            boolean result = stmt.executeUpdate() > 0;

            if (result){
                writer.println("true");
                //resp.sendError(200);

            }else {
                writer.println("false");
                //resp.sendError(400);

            }

        }catch (SQLException e) {

            resp.sendError(500);

            e.printStackTrace();

        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
Connection connection=null;
        try{

            ServletInputStream is = req.getInputStream();
            JsonReader reader = Json.createReader(is);
            JsonObject customer = reader.readObject();

            String name = customer.getString("name");
            String address = customer.getString("address");
            //double salary = customer.getJsonNumber("salary").doubleValue();

            BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
            connection = bds.getConnection();

            PreparedStatement stmt = connection.prepareStatement("UPDATE customer SET name=?,address=? WHERE id=?");

            stmt.setObject(3,pathInfo.substring(1));
            stmt.setObject(1,name);
            stmt.setObject(2,address);
            //stmt.setObject(3,salary);

            boolean result = stmt.executeUpdate() > 0;

            if (result){
                writer.println("true");
         //       resp.sendError(200);

            }else {
                writer.println("false");
                //resp.sendError(400);

            }

        } catch (SQLException e) {

            resp.sendError(500);

            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}