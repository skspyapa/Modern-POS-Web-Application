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
@WebServlet(urlPatterns = "/item/*")
public class ItemServlet extends HttpServlet {
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
                PreparedStatement stmt = connection.prepareStatement("SELECT * from item");

                ResultSet resultSet = stmt.executeQuery();

                if(resultSet.isBeforeFirst()) {

                    while (resultSet.next()) {

                        String code = resultSet.getString(1);
                        String description = resultSet.getString(2);
                        double unitPrice = resultSet.getDouble(3);
                        int qtyOnHand = resultSet.getInt(4);

                        ob.add("code", code);
                        ob.add("description", description);
                        ob.add("unitPrice", unitPrice);
                        ob.add("qtyOnHand", qtyOnHand);
                        ab.add(ob.build());
                    }

                    JsonArray itemsArray = ab.build();
                    writer.println(itemsArray.toString());

                }else {

                    resp.sendError(404);
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

        }else{

            try {

                BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
                connection = bds.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT * from item where code=?");

                stmt.setObject(1,pathInfo.substring(1));

                ResultSet resultSet = stmt.executeQuery();

                if(resultSet.next()) {

                    String code = resultSet.getString(1);
                    String description = resultSet.getString(2);
                    double unitPrice = resultSet.getDouble(3);
                    int qtyOnHand = resultSet.getInt(4);

                    ob.add("code", code);
                    ob.add("description", description);
                    ob.add("unitPrice", unitPrice);
                    ob.add("qtyOnHand", qtyOnHand);

                    JsonObject itemsArray = ob.build();
                    writer.println(itemsArray.toString());

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
            JsonObject item = reader.readObject();

            String code = item.getString("code");
            String description = item.getString("description");
            double unitPrice = item.getJsonNumber("unitPrice").doubleValue();
            int qtyOnHand = item.getInt("qtyOnHand");

            if (code == null || description == null) {

                resp.sendError(400);

            } else {
                BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
                connection = bds.getConnection();
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO item set code=?,description=?,unitPrice=?,qtyOnHand=?");

                stmt.setObject(1,code);
                stmt.setObject(2,description);
                stmt.setObject(3,unitPrice);
                stmt.setObject(4,qtyOnHand);

                boolean result = stmt.executeUpdate() > 0;

                if (result){
writer.println("true");
                    //resp.sendError(200);

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
            PreparedStatement stmt = connection.prepareStatement("DELETE from item where code=?");

            stmt.setObject(1,pathInfo.substring(1));

            boolean result = stmt.executeUpdate() > 0;

            if (result){
writer.println("true");
//                resp.sendError(200);

            }else {
writer.println("false");
//                resp.sendError(400);

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
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        resp.setContentType("application/json");
Connection connection=null;
        try{

            ServletInputStream is = req.getInputStream();
            JsonReader reader = Json.createReader(is);
            JsonObject item = reader.readObject();

            String description = item.getString("description");
            double unitPrice = item.getJsonNumber("unitPrice").doubleValue();
            int qtyOnHand = item.getInt("qtyOnHand");

            BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
            connection = bds.getConnection();
            PreparedStatement stmt = connection.prepareStatement("UPDATE item SET description=?,unitPrice=?,qtyOnHand=? WHERE code=?");

            stmt.setObject(4,pathInfo.substring(1));
            stmt.setObject(1,description);
            stmt.setObject(2,unitPrice);
            stmt.setObject(3,qtyOnHand);

            boolean result = stmt.executeUpdate() > 0;

            if (result){

                resp.sendError(200);

            }else {

                resp.sendError(400);

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
}
