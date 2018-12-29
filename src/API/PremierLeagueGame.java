package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;

public class PremierLeagueGame implements Game{
    private int day;
    private int month;
    private LocalTime time;
    private int gameTime;
    private int homeTeamID;
    private int awayTeamID;
    private int gameID;
    private int homeScore;
    private int awayScore;
    private int status;
    private GameType type = GameType.PREM;

    public PremierLeagueGame(String line, int id){
        boolean homeAway = false;
        this.status = -1;
        if (line.contains("is_home")){
            int start = line.indexOf("is_home", line.indexOf("current_event_fixture"));
            int end = line.indexOf(",", start);
            if (line.substring(start+9,end).equals("true")){
                homeAway = true;
                this.homeTeamID = id;
            } else {
                this.awayTeamID = id;
            }
        }
        if (line.contains("opponent")){
            //int start = line.indexOf("opponent", line.indexOf("next_event_fixture"));
            int start = line.indexOf("opponent", line.indexOf("current_event_fixture"));
            int end = line.indexOf("}", start);
            if (homeAway){
                this.awayTeamID = Integer.parseInt(line.substring(start+10, end));
            } else {
                this.homeTeamID = Integer.parseInt(line.substring(start+10, end));
            }
        }
        if (line.contains("month")){
            //int start = line.indexOf("month", line.indexOf("next_event_fixture"));
            int start = line.indexOf("month", line.indexOf("current_event_fixture"));
            int end = line.indexOf(",", start);
            this.month = Integer.parseInt(line.substring(start+7, end));
        }
        if (line.contains("day")){
            //int start = line.indexOf("\"day\"", line.indexOf("next_event_fixture"));
            int start = line.indexOf("\"day\"", line.indexOf("current_event_fixture"));
            int end = line.indexOf(",", start);
            this.day = Integer.parseInt(line.substring(start+6, end));
        }
        //if (line.contains("next_event_fixture")){
        if (line.contains("current_event_fixture")){
            //int start = line.indexOf("id", line.indexOf("next_event_fixture"));
            int start = line.indexOf("id", line.indexOf("current_event_fixture"));
            int end = line.indexOf(",", start);
            this.gameID = Integer.parseInt(line.substring(start+4, end));
        }
        try {
            this.update();
        } catch (IOException e){
            System.out.println("API.PremierLeagueGame() " + e.getMessage());
        }
    }


    /*
    NOTE: The API does not update minutes. Goals, however, are updated in real time.
     */
    @Override
    public void update() throws IOException {
        URL leagueTeams = new URL("https://fantasy.premierleague.com/drf/fixtures");
        BufferedReader in = new BufferedReader(new InputStreamReader(leagueTeams.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            int gameStart = inputLine.indexOf("\"id\":" + gameID);
            int gameEnd = inputLine.indexOf("\"id\":", gameStart+1);
            if (gameStart > 0 && gameStart < gameEnd){
                String line = inputLine.substring(gameStart, gameEnd);
                if (line.contains("kickoff")){
                    int start = line.indexOf("kickoff");
                    this.time = LocalTime.of(Integer.parseInt(line.substring(start+32, start+34)), Integer.parseInt(line.substring(start+35, start+37)));
                }
                if (line.contains("minutes")){
                    int start = line.indexOf("minutes");
                    int end = line.indexOf(",", start);
                    this.gameTime = Integer.parseInt(line.substring(start+9, end));
                }
                if (line.contains("started")){
                    int start = line.indexOf("started");
                    int end = line.indexOf(",", start);
                    if (line.substring(start+9, end).equals("true")){
                        status = 0;
                    }
                }
                if (line.contains("finished_provisional")){
                    int start = line.indexOf("finished_provisional");
                    int end = line.indexOf(",", start);
                    if (line.substring(start+22, end).equals("true")){
                        status = 1;
                    }
                }
                if (line.contains("team_h_score") && status >= 0){
                    int start = line.indexOf("team_h_score");
                    int end = line.indexOf(",", start);
                    if (!line.substring(start+14, end).equals(null)){
                        this.homeScore = Integer.parseInt(line.substring(start+14, end));
                    }else{
                        this.awayScore = 0;
                    }
                }
                if (line.contains("team_a_score") && status >= 0){
                    int start = line.indexOf("team_a_score");
                    int end = line.indexOf(",", start);
                    if (!line.substring(start+14, end).equals(null)){
                        this.awayScore = Integer.parseInt(line.substring(start+14, end));
                    }else{
                        this.awayScore = 0;
                    }

                }
            }
        }
        in.close();
    }

    @Override
    public String getScore(){
        try {
            update();
        } catch (IOException e){
            System.out.println("getScore " + e.getMessage());
        }
        if (status == 0){
            return awayTeamID + ": " + awayScore + " @ " + homeTeamID + ": " + homeScore + " at " + gameTime + " minutes";
        }
        else if (status == -1){
            return awayTeamID + " @ " + homeTeamID + " on " + month + "/" + day + " at " + time;
        }
        else {
            return "FINAL: " + awayTeamID + ": " + awayScore + " @ " + homeTeamID + ": " + homeScore + " on " + month + "/" + day;
        }
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Game){
            return this.homeTeamID == ((Game) o).getHomeTeam() && this.awayTeamID == ((Game) o).getAwayTeam();
        }
        return false;
    }

    @Override
    public String getGameTime(){
        return "n/a";
    }

    @Override
    public String toString(){
        return awayTeamID + " @ " + homeTeamID + " on " + month + "/" + day;
    }

    @Override
    public int getHomeTeam(){
        return homeTeamID;
    }
    @Override
    public int getAwayTeam(){
        return awayTeamID;
    }
    @Override
    public GameType getType(){
        return type;
    }

    @Override
    public int getHomeScore() {
        return homeScore;
    }

    @Override
    public int getAwayScore() {
        return awayScore;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public LocalTime getTime() {
        return time;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
