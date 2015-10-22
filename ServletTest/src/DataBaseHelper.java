import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.datamodel.datamodels.*;
import com.google.android.gms.maps.model.LatLng;

public class DataBaseHelper {

    final String myDriver = "com.mysql.jdbc.Driver";
    final String myUrl = "jdbc:mysql://mysql48.1gb.ru/gb_cleangames";
    final String User = "gb_cleangames";
    final String Pass = "a5a23237psg";


    public static void main(String[] args) throws ParseException {
    }

    Connection connection = null;
    Statement stmt = null;

    private void openConn() throws ClassNotFoundException, SQLException {
        Class.forName(myDriver);
        connection = DriverManager.getConnection(myUrl, User, Pass);
    }

    private ResultSet getResultSet(String query) throws SQLException {
        Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = st.executeQuery(query);
        preparedStatement.execute();
        return resultSet;
    }

    public ArrayList<Game> GetGameList() {
        ArrayList<Game> listProjects;
        try {
            openConn();

            listProjects = loadProjectsWithoutLocations();

            for (Game game : listProjects) {
                ResultSet resultSetLocations = getResultSet(
                        "select Comment, Type, PlaceX, PlaceY " +
                                "from Locations " +
                                "where ProjectID = " + game.getID());
                while (resultSetLocations.next())
                {
                    Location location = new Location(resultSetLocations.getString("Comment"),
                            (resultSetLocations.getInt("Type") == 0 ? LocationRole.BASE : LocationRole.WAREHOUSE ),
                            new LatLng(resultSetLocations.getDouble("PlaceX"),
                                    resultSetLocations.getDouble("PlaceY")));
                    game.getBase_loc().add(location);
                }
                resultSetLocations.close();
            }

            return listProjects;
        } catch (Exception e) {
            e.printStackTrace();
            ArrayList<Game> dummyError = new ArrayList<Game>();
            dummyError.add(new Game(Integer.parseInt(e.toString()), e.toString(), new LatLng(0.0, 0.0)));
            return dummyError;
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    private ArrayList<Game> loadProjectsWithoutLocations() throws SQLException {
        ArrayList<Game> listProjects = new ArrayList<Game>();
        ResultSet resultSetProject = getResultSet("select ID, Name from Projects");
        while (resultSetProject.next()) {
            Game game = new Game(resultSetProject.getInt("ID"), resultSetProject.getString("Name"), new ArrayList<Location>());
            listProjects.add(game);
        }
        resultSetProject.close();
        return listProjects;
    }



    public List<CheckIn> GetCheckinList(int projectID) {
        List<CheckIn> listCheckIn;
        try {
            openConn();

            listCheckIn = loadCheckinWithoutItems(projectID);

            for (CheckIn checkin:listCheckIn) {
                ResultSet resultSetGarbage = getResultSet(
                        "select ci.ID, ci.Value, par.Name " +
                                "from CheckinItem ci " +
                                "left join Parameters par ON par.ID = ci.ParametersID " +
                                "where ci.CheckinID = " + checkin.getID());
                while (resultSetGarbage.next())
                {
                    Param param = new Param(resultSetGarbage.getString("Name"), resultSetGarbage.getInt("Value"));
                    checkin.getGarb_param().add(param);
                }

                resultSetGarbage.close();
            }

            return listCheckIn;
        } catch (Exception e) {
            e.printStackTrace();
            List<CheckIn> dummyError = new ArrayList<CheckIn>();
            dummyError.add(new CheckIn(e.toString(), new LatLng(0.0, 0.0)));
            return dummyError;
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    private List<CheckIn> loadCheckinWithoutItems(int projectID) throws SQLException {
        List<CheckIn> listCheckin = new ArrayList<CheckIn>();
        ResultSet resultSetChickin = getResultSet("select ID, PlaceX, PlaceY, Comment from Checkin where ProjectID = " + projectID);

        while (resultSetChickin.next()) {
            CheckIn checkin = new CheckIn(resultSetChickin.getInt("ID"), resultSetChickin.getString("Comment"), new ArrayList<Param>(),
                    new LatLng(resultSetChickin.getDouble("PlaceX"), resultSetChickin.getDouble("PlaceY")), 5, 6);
            listCheckin.add(checkin);
        }

        resultSetChickin.close();
        return listCheckin;
    }

    public List<User> GetTeamParticipants(int teamID) {
        List<User> teamParticipants = new ArrayList<User>();
        try {
            openConn();


            ResultSet rs1 = getResultSet("select ID, Name, Surname, IsAdmin from User where TeamID =" + teamID);
            while (rs1.next()) {
                User user = new User(rs1.getInt("ID"),rs1.getString("Name"),rs1.getString("Surname"), rs1.getBoolean("IsAdmin"));
                teamParticipants.add(user);
            }
            rs1.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return teamParticipants;
    }

    public List<CheckIn> GetUserCheckinList(int projectID, int userID)
    {
        List<CheckIn> userCheckinList;
        try{
            openConn();
            userCheckinList = loadUserCheckinListWithoutItems(projectID, userID);
            for (CheckIn checkIn:userCheckinList)
            {
                ResultSet resultSet = getResultSet("select ci.ID, ci.Value, par.Name from " +
                        "CheckinItem ci left join Parameters par ON par.ID = ci.ParametersID where ci.CheckinID = 1"); //+checkIn.getID());
                //);
                while (resultSet.next())
                {
                    Param param = new Param(resultSet.getString("Name"), resultSet.getInt("Value"));
                    checkIn.getGarb_param().add(param);
                }

                resultSet.close();

            }
            return userCheckinList;
        }
        catch (Exception e) {
            e.printStackTrace();
            List<CheckIn> dummyError = new ArrayList<CheckIn>();
            dummyError.add(new CheckIn(e.toString(), new LatLng(0.0, 0.0)));
            return dummyError;
        }
        finally
        {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }



    public void CreateTransferItem(int transferID, int parameterID, double value) {

        try {
            openConn();

            String query = "insert into TransferItem (GarbageTransferID, ParametersID, Value)" + " values (?, ?, ?)";

            PreparedStatement pS = connection.prepareStatement(query);
            pS.setInt(1, transferID);
            pS.setInt(2, parameterID);
            pS.setDouble(3, value);

            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public int CreateTransfer(String teamName, int locationID) {
        int id = 0;
        try {
            openConn();

            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String sqlQuery = "select ID from Team where Name = '" + teamName + "'";
            ResultSet rs = st.executeQuery(sqlQuery);
            rs.next();
            int teamID = rs.getInt("ID");


            String query = "insert into Transfer (TeamID, LocationID, CreatedTime)" + " values (?, ?, ?)";
            long timeNow = Calendar.getInstance().getTimeInMillis();
            java.sql.Timestamp startDate = new java.sql.Timestamp(timeNow);

            PreparedStatement pS = connection.prepareStatement(query);
            pS.setInt(1, teamID);
            pS.setInt(2, locationID);
            pS.setTimestamp(3, startDate);
            sqlQuery = "select ID from Transfer where Name = '" + teamName + "'";
            rs = st.executeQuery(sqlQuery);
            rs.next();
            id = rs.getInt("ID");
            rs.close();

            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return id;
    }

    //�������� ������ ���������� ������
   /* private List<String> GetParametersList() {
        List<String> parametersList = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(myUrl, User, Pass);
            System.out.println("Connected to database succesfully...");

            Statement st1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String SQLquery = "select Name from Parameters";
            PreparedStatement ps1 = connection.prepareStatement(SQLquery);
            ResultSet rs1 = st1.executeQuery(SQLquery);
            ps1.execute();
            while (rs1.next()) {
                String teamName = rs1.getString("Name");
                parametersList.add(teamName);
            }
            rs1.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return parametersList;
    }*/

    //�������� ������ ���� ������
    /*private List<String> GetTeamList() {
        List<String> teamList = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(myUrl, User, Pass);
            System.out.println("Connected to database succesfully...");

            Statement st1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String SQLquery = "select Name from Team";
            PreparedStatement ps1 = connection.prepareStatement(SQLquery);
            ResultSet rs1 = st1.executeQuery(SQLquery);
            ps1.execute();
            while (rs1.next()) {
                String teamName = rs1.getString("Name");
                teamList.add(teamName);
            }
            rs1.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return teamList;
    }*/

    //�������� ������ ���������� �������


    //������� �������
    public void CreateTeam(String teamName ) {

        try {
            openConn();

            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String sqlQuery = "select count(*) from Team where Name = '" + teamName + "'";
            ResultSet rs = st.executeQuery(sqlQuery);
            int count = 0;
            rs.next();
            count = rs.getInt(1);
            rs.close();

            if (count == 0) {
                String query = "insert into Team (Name, CreatedTime)" + " values (?, ?)";
                long timeNow = Calendar.getInstance().getTimeInMillis();
                java.sql.Timestamp startDate = new java.sql.Timestamp(timeNow);
                PreparedStatement pS = connection.prepareStatement(query);
                pS.setString(1, teamName);
                pS.setTimestamp(2, startDate);
                pS.execute();

            } else {

            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    //�������� �������� �������
    public void UpdateTeamName(String oldTeamName, String newTeamName) {

        try {
            openConn();

            String query = "update Team" + " set Name = ? where Name = '" + oldTeamName + "'";

            PreparedStatement pS = connection.prepareStatement(query);
            pS.setString(1, newTeamName);

            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    //������� �������
    public void DeleteTeam(String teamName) {

        try {
            openConn();

            String query = "delete from Team" + " where Name = '" + teamName + "'";

            PreparedStatement pS = connection.prepareStatement(query);
            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public void CreateGarbageParameter(int projectID, String parameterName, double price) {

        try {
            openConn();

            String query = "insert into Parameters (ProjectID, Name, Price)" + " values (?, ?, ?)";

            PreparedStatement pS = connection.prepareStatement(query);
            pS.setInt(1, projectID);
            pS.setString(2, parameterName);
            pS.setDouble(3, price);

            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void UpdateGarbageParameter(String garbageParameter, double newPrice) {

        try {
            openConn();

            String query = "update Parameters" + " set Price = ? where Name = '" + garbageParameter + "'";

            PreparedStatement pS = connection.prepareStatement(query);
            pS.setDouble(1, newPrice);

            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void DeleteParameter(String parameterName) {

        try {
            openConn();

            String query = "delete from Parameters" + " where Name = '" + parameterName + "'";

            PreparedStatement pS = connection.prepareStatement(query);

            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public int SignUpUser(String email, String password, String userName, String userSurname) {
        int answ = 2;

        try {
            openConn();
            /*Statement st1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String SQLquery = "select count(*) from User where Email = '" + email + "'";
            PreparedStatement ps1 = connection.prepareStatement(SQLquery);
            ResultSet rs1 = st1.executeQuery(SQLquery);
            ps1.execute();
            rs1.next();
            int count = rs1.getInt(1);

            if (count == 1) {
                String query = "insert into User (Email, Password, Name, Surname, IsAdmin, CreatedTime)" + " values (?, ?, ?, ?, ?, ?)";
                Calendar calendar = Calendar.getInstance();
                java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

                PreparedStatement pS = connection.prepareStatement(query);
                pS.setString(1, email);
                pS.setString(2, password);
                pS.setString(3, userName);
                pS.setString(4, userSurname);
                pS.setBoolean(5, true);
                pS.setDate(6, startDate);
                pS.execute();

            } else {*/

            Statement st1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String SQLquery = "select count(*) from User where Email = '" + email + "'";
            PreparedStatement ps1 = connection.prepareStatement(SQLquery);
            ResultSet rs1 = st1.executeQuery(SQLquery);
            ps1.execute();
            rs1.next();
            int count = rs1.getInt(1);

            if (count == 0) {
                answ = 0;
                String query = "insert into User (Email, Password, Name, Surname, IsAdmin, CreatedTime)" + " values (?, ?, ?, ?, ?, ?)";

                long timeNow = Calendar.getInstance().getTimeInMillis();
                java.sql.Timestamp startDate = new java.sql.Timestamp(timeNow);
                //Calendar calendar = Calendar.getInstance();
                //java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

                PreparedStatement pS = connection.prepareStatement(query);
                pS.setString(1, email);
                pS.setString(2, password);
                pS.setString(3, userName);
                pS.setString(4, userSurname);
                pS.setBoolean(5, false);
                pS.setTimestamp(6, startDate);
                pS.execute();
            } else {
                answ = 1;
            }
            //}

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return answ;
    }

    private int SignInUser(String email, String password) {

        int answ = 3;
        try {
            openConn();

            Statement st1 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String SQLquery = "select count(*) from User where Email = '" + email + "'";
            PreparedStatement ps1 = connection.prepareStatement(SQLquery);
            ResultSet rs1 = st1.executeQuery(SQLquery);
            ps1.execute();
            rs1.next();
            int count = rs1.getInt(1);

            if (count == 1) {
                String query = "select Password from User where Email = '" + email + "'";
                PreparedStatement pS = connection.prepareStatement(query);
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(query);
                pS.execute();
                rs.next();
                String pass = rs.getString("Password");
                if (pass.equals(password)) {
                    answ = 0;
                } else {
                    answ = 2;
                }
            } else {
                answ = 1;
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return answ;
    }

    public void JoinTeam(int userID, int teamID, int maxCount) {

        try {
            openConn();
            ResultSet rs = getResultSet("select count(*) from User where TeamID = " + teamID);
            rs.next();
            int count = rs.getInt(1);
            rs.close();

            if (count <= maxCount) {
                String query = "update User set TeamID = " + teamID + " where ID = " + userID;
                PreparedStatement pS = connection.prepareStatement(query);
                pS.execute();
            } else {
                System.out.println("Maximum team participants");
            }


        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

   public void LeaveTeam(int userID) {

        try {
            openConn();

            String query = "update User set TeamID = NULL" + " where ID = " + userID;

            PreparedStatement pS = connection.prepareStatement(query);
            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void CreateLocation(int type, double placeX, double placeY, int projectID, String comment) {

        try {
            openConn();

            String query = "insert into Locations (Type, PlaceX, PlaceY, ProjectID, Comment)" + " values (?, ?, ?, ?, ?)";

            PreparedStatement pS = connection.prepareStatement(query);
            pS.setInt(1, type);
            pS.setDouble(2, placeX);
            pS.setDouble(3, placeY);
            pS.setInt(4, projectID);
            pS.setString(5, comment);

            pS.execute();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    private void TestSelect(int teamID) {

        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(myUrl, User, Pass);
            System.out.println("Connected to database succesfully...");


            String query = "select Name from Team where ID = " + teamID;
            PreparedStatement pS = connection.prepareStatement(query);

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            pS.execute();

            while (rs.next()) {
                String name = rs.getString("Name");
                System.out.println(name + "\n");
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    connection.close();
            } catch (SQLException se) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private List<CheckIn> loadUserCheckinListWithoutItems(int projectID, int userID) throws SQLException {
        List<CheckIn> listCheckin = new ArrayList<CheckIn>();
        ResultSet resultSetCheckin = getResultSet("select ID, PlaceX, PlaceY, Comment from Checkin where ProjectID = " + projectID + " and UserID = " + userID);

        while (resultSetCheckin.next()) {
            CheckIn checkin = new CheckIn(resultSetCheckin.getString("Comment"), new ArrayList<Param>(),
                    new LatLng(resultSetCheckin.getDouble("PlaceX"), resultSetCheckin.getDouble("PlaceY")));
            listCheckin.add(checkin);
        }

        resultSetCheckin.close();
        return listCheckin;
    }


}