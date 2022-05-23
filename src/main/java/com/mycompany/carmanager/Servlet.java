/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.carmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest; import jakarta.servlet.http.HttpServletResponse;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author TeoTeo23_
 */
public class Servlet extends HttpServlet {
    private Connection connection = null;
    
    @Override
    public void init(){
        String url = "jdbc:mysql://localhost:3306/centrorevisioni";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, "root", "MatteCP23");
        }catch(SQLException | ClassNotFoundException exception){
            System.out.println("Error in loading Driver.\r\n");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        // Check for database connection
        response.setContentType("text/xml;charset=UTF-8");
        if (this.connection == null){
            response.sendError(500, "Unable to connect to the specified Database.\r\n");
            return;
        }
        // Setting variables for first request
        // Extract URL data
        String url = request.getRequestURL().toString();
        String[] sel = url.split("/");
        String end = sel[sel.length - 1];
        
        if(url.equalsIgnoreCase("http://localhost:8080/CarManager/Servlet")){
            response.sendError(400, "Missing parameters.\r\n");
            return;
        }
        
        if(end.equalsIgnoreCase("Expiring")){
        String startDate = request.getParameter("start");
        String endDate = request.getParameter("end");
        /* if(sM == null || sY == null || lM == null || lY == null || sM.isBlank() || sY.isBlank() || lM.isBlank() || lY.isBlank()){ // missing params?
            response.sendError(400, "Missing parameters.\r\n");
            return;
        }
        */
        if(startDate == null || endDate == null || startDate.isBlank() || endDate.isBlank()){
            response.sendError(400, "Missing parameters.\r\n");
            return;
        }
        String sM = "", lM = "", sY = "", lY = ""; // Interval periods
        // Splitting interval periods 
        String[] desectBeginPeriod = startDate.split("/");
        String[] desectEndPeriod = endDate.split("/");
        // Initialize data in Periods params
        sM  = desectBeginPeriod[0];
        sY = desectBeginPeriod[1];
        lM = desectEndPeriod[0];
        lY = desectEndPeriod[1];      
        
        if(Integer.parseInt(sM) < 1 || Integer.parseInt(sM) > 12 || Integer.parseInt(lM) < 1 || Integer.parseInt(lM) > 12 || 
                Integer.parseInt(sY) > Integer.parseInt(lY)){
            response.sendError(400, "Bad time range.\r\n");
            return;
        }
            try{
                String query = "SELECT targa FROM revisioni WHERE mese_scadenza BETWEEN " + sM + " AND " + lM 
                        + " AND anno_scadenza BETWEEN " + sY + " AND " + lY + ";";
                Statement stat = connection.createStatement();
                ResultSet rs = stat.executeQuery(query);
                StringBuilder res = new StringBuilder();
                res.append("<revisions>\n");
                while(rs.next()){
                    res.append("<car>\n");
                    res.append("<plate>").append(rs.getString("targa")).append("</plate>\n");
                    res.append("</car>\n");
                }
                res.append("</revisions>\n");
                
                // System.out.println(res.toString());
                response.setStatus(200);
                
                // Send XML Body reply
                PrintWriter printWriter = new PrintWriter(response.getWriter());
                printWriter.write(res.toString());
                printWriter.flush();
                
                // Closing instances
                printWriter.close();
                rs.close();
                stat.close();
            }catch(SQLException exception){ 
                response.sendError(500, "Unable to execute operation 'EXPIRING'.\r\n"); 
                return;
            }
        }
        
        else if(end.equalsIgnoreCase("info")){
            String plate = request.getParameter("plate");
            if(plate == null || plate.isBlank()){ // missing params?
                response.sendError(400, "Missing parameter.\r\n");
                return;
            }
            
            try{
                String query = "SELECT data_revisione, mese_scadenza, anno_scadenza, esito FROM revisioni WHERE targa = '" + plate + "';";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                StringBuilder builder = new StringBuilder();
                builder.append("<revision>\n");
                while(resultSet.next()){
                    builder.append("<revisionDate>").append(resultSet.getString("data_revisione")).append("</revisionDate>\n");
                    builder.append("<expireMonth>").append(resultSet.getString("mese_scadenza")).append("</expireMonth>\n");
                    builder.append("<expireYear>").append(resultSet.getString("anno_scadenza")).append("</expireYear>\n");
                    builder.append("<esit>").append(resultSet.getString("esito")).append("</esit>\n");
                }
                builder.append("</revision>");
                // Send XML Body reply
                PrintWriter pw = new PrintWriter(response.getWriter());
                pw.write(builder.toString());
                pw.flush();
                // Closing instances
                statement.close();
                resultSet.close();
                pw.close();
            }catch(SQLException exception){ response.sendError(500, "Unable to execute operation 'INFO'.\r\n"); }
        }
        
        else if(end.equalsIgnoreCase("revisions")){
            String plate = request.getParameter("plate");
            if(plate == null || plate.isBlank()){
                response.sendError(400, "Missing parameter.\r\n");
                return;
            }
            
            try{
                String query = "SELECT * FROM revisioni WHERE targa = '" + plate + "';";
                Statement statement = connection.createStatement();
                ResultSet resultset = statement.executeQuery(query);
                StringBuilder builder = new StringBuilder();
                
                builder.append("<revision>\n");
                while(resultset.next()){
                    builder.append("<car>\n");
                    builder.append("<plate>").append(resultset.getString("targa")).append("</plate>\n");
                    builder.append("<matricolation>").append(resultset.getString("anno_immatricolazione")).append("</matricolation>\n");
                    builder.append("<revDate>").append(resultset.getString("data_revisione")).append("</revDate>\n");
                    builder.append("<expireMonth>").append(resultset.getString("mese_scadenza")).append("</expireMonth>\n");
                    builder.append("<expireYear>").append(resultset.getString("anno_scadenza")).append("</expireYear>");
                    builder.append("<esit>").append(resultset.getString("esito")).append("</esit>");
                    builder.append("</car>\n");
                }
                builder.append("</revision>");
                
                PrintWriter writer = new PrintWriter(response.getWriter());
                writer.write(builder.toString());
                writer.flush();
                
                statement.close();
                resultset.close();
                writer.close();
            }catch(SQLException exception){ response.sendError(500, "Unable to execute operation 'REVISIONS'.\r\n"); }
        }
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        if(this.connection == null) response.sendError(500, "Unable to connect to the specified database.\r\n");
        String url = request.getRequestURL().toString();
        String[] sel = url.split("/");
        String end = sel[sel.length - 1];
        Converter converter = new Converter();
      
        if(end.equalsIgnoreCase("new")){
            String plate = "", revDate = "", esit = "";
            int matricolation = -1, expireMonth = -1, expireYear = -1;
            
            try{
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(request.getInputStream());
                Element root = doc.getDocumentElement();
                NodeList plateList, revDateList, esitList, matricolationList, expireMonthList, expireYearList;
                
                plateList = root.getElementsByTagName("plate");
                if(plateList != null && plateList.getLength() > 0)
                    plate = plateList.item(0).getTextContent();
                
                revDateList = root.getElementsByTagName("revDate");
                if(revDateList != null && revDateList.getLength() > 0)
                    revDate = revDateList.item(0).getTextContent();
                
                esitList = root.getElementsByTagName("esit");
                if(esitList != null && esitList.getLength() > 0)
                    esit = esitList.item(0).getTextContent();
                
                matricolationList = root.getElementsByTagName("matricolation");
                if(matricolationList != null && matricolationList.getLength() > 0)
                    matricolation = Integer.parseInt(matricolationList.item(0).getTextContent());
                
                expireMonthList = root.getElementsByTagName("expireMonth");
                if(expireMonthList != null && expireMonthList.getLength() > 0)
                    expireMonth = Integer.parseInt(expireMonthList.item(0).getTextContent());
                
                expireYearList = root.getElementsByTagName("expireYear");
                if(expireYearList != null && expireYearList.getLength() > 0)
                    expireYear = Integer.parseInt(expireMonthList.item(0).getTextContent());
                
                if(plate.isBlank() || revDate.isBlank() || esit.isBlank() || matricolation == - 1 || expireMonth == -1 || expireYear == -1){
                    response.sendError(400, "Missing parameter(s).\r\n");
                    return;
                }
                
                // Convert Int to SQL date
                java.sql.Date date = converter.stringToDate(revDate);
                String query = "INSERT INTO revisioni(targa, anno_immatricolazione, data_revisione, mese_scadenza, anno_scadenza, esito) VALUES(?, ?, ?, ?, ?, ?);";
                try (PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setString(1, plate);
                    ps.setInt(2, matricolation);
                    ps.setDate(3, date);
                    ps.setInt(4, expireMonth);
                    ps.setInt(5, expireYear);
                    ps.setString(6, esit);
                    System.out.println(ps.toString());
                    if(ps.executeUpdate() == 0){
                        response.sendError(500, "Insertion failed.\r\n");
                        return;
                    }
                    response.setStatus(200);
                }
            }catch(ParserConfigurationException | SAXException exception){ response.sendError(400, "Malformed XML.\r\n"); }
            catch(SQLException exception){ response.sendError(500, "Unable to execute operation 'INSERT'"); }
        }else
            response.sendError(400, "Error in requesting command.\r\n");
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        if(connection == null){
            response.sendError(500, "Unable to connect to specified database.\r\n");
            return;
        }
        
        String[] sel = request.getRequestURL().toString().split("/");
        String end = sel[sel.length - 1];
        
        if(end.equalsIgnoreCase("Delete")){
            String plate = request.getParameter("plate");
            if(plate == null || plate.isBlank()){
                response.sendError(400, "Missing parameter.\r\n");
                return;
            }
            try{
                String query = "DELETE FROM revisioni WHERE targa = '" + plate + "';";
                PreparedStatement ps = connection.prepareStatement(query);
                if(ps.executeUpdate() > 0)
                    response.setStatus(200);
                else{
                    response.sendError(400, "Missing plate.\r\n");
                    return;
                }
            ps.close();
            connection.close();
            }catch(SQLException exception){ response.sendError(500, "Unable to execute 'DELETE' operation.\r\n"); }
        }
    }
    
    
    @Override
    public void destroy(){
        try{
            if(!(connection == null))
                connection.close();
        }catch(SQLException exception){}
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
