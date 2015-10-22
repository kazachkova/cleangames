import com.datamodel.datamodels.CheckIn;
import com.datamodel.datamodels.Game;
import com.datamodel.datamodels.User;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;


public class Servlet extends HttpServlet {

    private static DataBaseHelper dbs = new DataBaseHelper();
    private static Gson gson = new Gson();

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/plain; charset=UTF-8");
        String actionType = request.getParameter("ActionType");

        if (actionType.equals("CreateTeam")) {
            String teamName = request.getParameter("TeamName");
            String userID = request.getParameter("UserID");
            dbs.CreateTeam(teamName);
            PrintWriter out = response.getWriter();
            out.println("Team added: " + teamName);

        }
        if (actionType.equals("UpdateTeam")) {
            String oldTeamName = request.getParameter("OldTeamName");
            String newTeamName = request.getParameter("NewTeamName");
            dbs.UpdateTeamName(oldTeamName,newTeamName);
            String str = "Team updated: " + oldTeamName + " -> " + newTeamName;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("DeleteTeam")){
            String teamName = request.getParameter("TeamName");
            dbs.DeleteTeam(teamName);
            String str = "Team deleted: " + teamName;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str, String.class, writer);
            writer.close();
        }
        if (actionType.equals("CreateGarbageParameter")) {
            String sProjectID = request.getParameter("ProjectID");
            int projectID = new Integer(sProjectID);
            String parameterName = request.getParameter("ParameterName");
            String sPrice = request.getParameter("Value");
            double price = new Double(sPrice);
            dbs.CreateGarbageParameter(projectID, parameterName, price);
            String str = "Parameter added: " + parameterName + ", Project ID = "
                    + projectID + ", Price = " + price;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("UpdateGarbageParameter")){
            String garbageParameter = request.getParameter("GarbageParameter");
            String sNewPrice = request.getParameter("NewPrice");
            double newPrice = new Double(sNewPrice);
            dbs.UpdateGarbageParameter(garbageParameter, newPrice);
            String str = "Parameter updated: " + garbageParameter + " new value: " + newPrice;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("DeleteGarbageParameter")){
            String garbageParameter = request.getParameter("ParameterName");
            dbs.DeleteParameter(garbageParameter);
            String str = "Parameter deleted: " + garbageParameter;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("SignUpUser")){
            String email = request.getParameter("Email");
            String password = request.getParameter("Password");
            String userName = request.getParameter("UserName");
            String userSurname = request.getParameter("UserSurname");
            dbs.SignUpUser(email, password, userName, userSurname);
            String str = "User signed up: " + email + " " + password + " "
                    + userName + " " + userSurname;
            response.setHeader("Content-Type", "text/plain; charset=UTF-8");
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("GetCheckinList")) {
            String sProjectID = request.getParameter("ProjectID");
            int projectID = new Integer(sProjectID);
            List<CheckIn> AnswerList = dbs.GetCheckinList(projectID);
            returnJsonArray(response, AnswerList, CheckIn.class);
        }
        if (actionType.equals("GetGameList")) {
            ArrayList<Game> AnswerList = dbs.GetGameList();
            returnJsonArray(response, AnswerList, Game.class);
        }
        if (actionType.equals("CreateTransferItem"))
        {
            String sTranferID = request.getParameter("TransferID");
            int transferID = new Integer(sTranferID);
            String sParameterID = request.getParameter("ParameterID");
            int parameterID = new Integer(sParameterID);
            String sValue = request.getParameter("Value");
            double value = new Double(sValue);
            dbs.CreateTransferItem(transferID,parameterID,value);
            String str = "Transfer iteam created: " + transferID + ' ' + parameterID + ' ' + value;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("CreateTransfer"))
        {
            String teamName = request.getParameter("TeamName");
            String sLocationID = request.getParameter("LocationID");
            int locationID = new Integer(sLocationID);
            dbs.CreateTransfer(teamName,locationID);
            String str = "Transfer iteam created: " + teamName + ' ' + locationID;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("JoinTeam"))
        {
            String sUserID= request.getParameter("UserID");
            int userID = new Integer(sUserID);
            String sTeamID = request.getParameter("TeamID");
            int teamID = new Integer(sTeamID);
            int maxCount = 5;
            dbs.JoinTeam(userID,teamID,maxCount);

        }
        if (actionType.equals("LeaveTeam"))
        {
            String sID = request.getParameter("ID");
            int id = new Integer(sID);
            dbs.LeaveTeam(id);
            String str = "User left the team: " + id;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("CreateLocation"))
        {
            String sType = request.getParameter("Type");
            int type = new Integer(sType);
            String sPlaceX = request.getParameter("PlaceX");
            double placeX = new Double(sPlaceX);
            String sPlaceY = request.getParameter("PlaceY");
            double placeY = new Double(sPlaceY);
            String sProjectID = request.getParameter("ProjectID");
            int projectID = new Integer(sProjectID);
            String comment = request.getParameter("Comment");
            dbs.CreateLocation(type,placeX,placeY,projectID,comment);
            String str = "Location created: " + type + ' ' + placeX + ' ' + placeY +  ' ' + projectID + ' ' + comment;
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        if (actionType.equals("GetTeamParticipants"))
        {
            String sTeamID = request.getParameter("TeamID");
            int teamID = new Integer(sTeamID);
            List<User> teamParticipants = dbs.GetTeamParticipants(teamID);
            returnJsonArray(response, teamParticipants, User.class);
        }
        if (actionType.equals("GetUserCheckinList"))
        {
            String sPrjectID = request.getParameter("ProjectID");
            int projectID = new Integer(sPrjectID);
            String sUserID = request.getParameter("UserID");
            int userID = new Integer(sUserID);
            List<CheckIn> userCheckinList = dbs.GetUserCheckinList(projectID,userID);
            returnJsonArray(response, userCheckinList, CheckIn.class);
        }
        if (actionType.equals("GSON")){
            String str = request.getParameter("TeamName");
            response.setHeader("Content-Type", "text/plain; charset=UTF-8");
            Gson gson = new Gson();
            gson.toJson(str);
            OutputStream out = response.getOutputStream();
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
            gson.toJson(str,String.class,writer);
            writer.close();
        }
        /*else {
            String str = "Error";
            PrintWriter out = response.getWriter();
            //Gson gson = new Gson();
            //gson.toJson(str);
            out.println(str);
        }*/
    }

    private int getProjectID(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter("projectID"));
    }

    private <T> void returnJsonArray(HttpServletResponse response, List<T> list, Class typeOfSrc) throws IOException {
        JsonWriter writer = openWriter(response);
        writer.beginArray();
        for (T obj: list) {
            gson.toJson(obj, typeOfSrc, writer);
        }
        writer.endArray();
        writer.close();
    }

    private <T> void returnJsonObject(HttpServletResponse response, T object, Class typeOfSrc) throws IOException {
        JsonWriter writer = openWriter(response);
        gson.toJson(object, typeOfSrc, writer);
        writer.close();
    }

    private JsonWriter openWriter(HttpServletResponse response) throws IOException {
        OutputStream out = response.getOutputStream();
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
        writer.setIndent("  ");
        return writer;
    }
}