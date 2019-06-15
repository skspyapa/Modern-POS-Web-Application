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
import java.text.ParseException;
import java.text.SimpleDateFormat;

@WebServlet(urlPatterns = "/orders/*")
public class OrderServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        String pathInfo = req.getPathInfo();
Connection connection=null;
        if(pathInfo.substring(1)!=null){
        try {

            BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
            connection = bds.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement stmt2 = connection.prepareStatement("select itemCode,qty from itemdetail where orderId=?");
            stmt2.setObject(1,pathInfo.substring(1));
            ResultSet resultSet = stmt2.executeQuery();

            while (resultSet.next()){

                String itemCode = resultSet.getString(1);
                int qty = resultSet.getInt(2);


                    PreparedStatement stmt4 = connection.prepareStatement("update item set qtyOnHand=qtyOnHand+? where code=?");
                    stmt4.setObject(1,qty);
                    stmt4.setObject(2,itemCode);
                    boolean result1 = stmt4.executeUpdate() > 0;

                    if(!result1){
                        resp.setStatus(500);
                        writer.println("Something Wrong In Deleting Order");
                    }

            }

                PreparedStatement stmt3 = connection.prepareStatement("delete from itemdetail where orderId=?");
                stmt3.setObject(1,pathInfo.substring(1));
                boolean result2 = stmt3.executeUpdate() > 0;

                if (result2){

                    PreparedStatement stmt1 = connection.prepareStatement("DELETE from orders where id=?");

                    stmt1.setObject(1,pathInfo.substring(1));

                    boolean result3 = stmt1.executeUpdate() > 0;

                    if(result3){

                        connection.commit();

                        resp.setStatus(200);

                        writer.println("Successfully Updated");

                    }else{
                        connection.rollback();

                        resp.setStatus(400);

                        writer.println("Something Wrong");
                    }

                }else {

                    connection.rollback();

                    resp.setStatus(500);

                    writer.println("Something Wrong");

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

    }else {
            resp.sendError(400);
        }

    }

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
                PreparedStatement stmt = connection.prepareStatement("SELECT * from orders");
                ResultSet resultSet = stmt.executeQuery();

                while (resultSet.next()){
                    String id = resultSet.getString(1);
                    Date date = resultSet.getDate(2);
                    String customerId = resultSet.getString(3);
                    ob.add("id",id);
                    ob.add("date",String.valueOf(date));
                    ob.add("customer_Id",customerId);
                    ab.add(ob.build());
                }

                JsonArray customersArray = ab.build();
                writer.println(customersArray.toString());

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
        }else{

            try {
                BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
                connection = bds.getConnection();

                PreparedStatement stmt1 = connection.prepareStatement("SELECT o.id,o.customer_Id,o.date,sum(i.qty*i.unitPrice) from orders o INNER join itemdetail i where o.id=i.orderId and o.id like ? group by o.id");
                stmt1.setObject(1,pathInfo.substring(1)+"%");
                ResultSet resultSet1 = stmt1.executeQuery();

                while (resultSet1.next()) {

                    String id = resultSet1.getString(1);
                    String custId = resultSet1.getString(2);
                    Date date = resultSet1.getDate(3);
                    double total = resultSet1.getDouble(4);
                    ob.add("id",id);
                    ob.add("custId",custId);
                    ob.add("date",String.valueOf(date));
                    ob.add("total",total);
                    ab.add(ob.build());
                }


                JsonArray orderArray = ab.build();
                writer.println(orderArray.toString());

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
Connection connection=null;
        try {

            ServletInputStream is = req.getInputStream();
            JsonReader reader = Json.createReader(is);
            JsonObject orders = reader.readObject();
            String id = orders.getString("id");

            String LocalDate = orders.getString("date");

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            java.util.Date parse = sdf.parse(LocalDate);
            sdf.applyPattern("yyyy-MM-dd");
            String date = sdf.format(parse);

            String customer_id = orders.getString("customer_Id");
            JsonArray itemdetail = orders.getJsonArray("itemdetail");
            BasicDataSource bds = (BasicDataSource) getServletContext().getAttribute("dbpool");
            connection = bds.getConnection();
            if (id == null || date == null || customer_id == null || itemdetail==null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);

            } else {

                connection.setAutoCommit(false);
                PreparedStatement stmt1 = connection.prepareStatement("INSERT INTO orders set id=?,date=?,customer_Id=?");
                stmt1.setObject(1,id);
                stmt1.setObject(2,date);
                stmt1.setObject(3,customer_id);
                boolean result1 = stmt1.executeUpdate() > 0;

                if (result1){

                    for (JsonValue jsonValue:itemdetail) {

                        String itemCode = jsonValue.asJsonObject().getString("itemCode");
                        String orderId = jsonValue.asJsonObject().getString("orderId");
                        int qty = jsonValue.asJsonObject().getInt("qty");
                        double unitPrice = jsonValue.asJsonObject().getJsonNumber("unitPrice").doubleValue();

                        PreparedStatement stmt2 = connection.prepareStatement("INSERT  INTO itemdetail set itemCode=?,orderId=?,qty=?,unitPrice=?");
                        stmt2.setObject(1,itemCode);
                        stmt2.setObject(2,orderId);
                        stmt2.setObject(3,qty);
                        stmt2.setObject(4,unitPrice);

                        boolean result2 = stmt2.executeUpdate() > 0;

                        if(result2){

                            PreparedStatement stmt3 = connection.prepareStatement("UPDATE item set qtyOnHand=qtyOnHand-? where code=?");
                            stmt3.setObject(1,qty);
                            stmt3.setObject(2,itemCode);
                            boolean result3 = stmt3.executeUpdate() > 0;

                            if(!result3){
                                connection.rollback();
                                writer.println("false");
                            }
                        }else {
                            connection.rollback();

                            writer.println("false");
                            //resp.sendError(400);

                        }

                    }
                    writer.println("true");
                    connection.commit();
                }else {
                    connection.rollback();

                    writer.println("false");
                    //resp.sendError(400);

                }
            }

        }catch (SQLException e) {

            resp.sendError(500);

            e.printStackTrace();

        } catch (ParseException e) {
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
