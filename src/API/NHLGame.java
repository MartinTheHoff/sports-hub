package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;

public class NHLGame implements Game{
    private int day;
    private int month;
    private LocalTime time;
    private String period;
    private String periodTime;
    private String link;
    private String homeTeam;
    private String awayTeam;
    private int homeTeamID;
    private int awayTeamID;
    private int homeScore;
    private int awayScore;
    /** Status is -1 before game starts, 0 during game, 1 after game */
    private int status;
    private GameType type = GameType.NHL;

    public NHLGame(String link) throws IOException{
        homeTeamID = -1;
        awayTeamID = -1;
        this.link = link;
        URL leagueTeams = new URL(link);
        BufferedReader in = new BufferedReader(new InputStreamReader(leagueTeams.openStream()));
        String inputLine;

        int nameCount = 0;

        while ((inputLine = in.readLine()) != null) {
            String[] lines = inputLine.split("\n");
            for (String line : lines) {
                if (line.contains("name")){
                    nameCount++;
                }
                else if (line.contains("dateTime")) {
                    int colon = line.indexOf(":");
                    this.time = LocalTime.of(Integer.parseInt(line.substring(colon+14, colon+16)), Integer.parseInt(line.substring(colon+17, colon+19)));
                    this.month = Integer.parseInt(line.substring(colon+8, colon+10));
                    this.day = Integer.parseInt(line.substring(colon+11, colon+13))-1;
                }
                else if (line.contains("id\"") && awayTeamID == -1) {
                    int colon = line.indexOf(":");
                    this.awayTeamID= Integer.parseInt(line.substring(colon+2, line.length()-1));
                }
                else if (line.contains("id\"") && nameCount == 5 && homeTeamID == -1) {
                    int colon = line.indexOf(":");
                    this.homeTeamID= Integer.parseInt(line.substring(colon+2, line.length()-1));
                }
                else if (nameCount == 1 && line.contains("name")) {
                    int colon = line.indexOf(":");
                    this.awayTeam = line.substring(colon+3, line.length()-2);
                }
                else if (nameCount == 6 && line.contains("name")) {
                    int colon = line.indexOf(":");
                    this.homeTeam = line.substring(colon+3, line.length()-2);
                }
                else if (line.contains("abstractGameState")) {
                    int colon = line.indexOf(":");
                    if (line.substring(colon+3, line.length()-2).equals("Preview")){
                        this.status = -1;
                    }
                    if (line.substring(colon+3, line.length()-2).equals("Live")){
                        this.status = 0;
                    }
                    if (line.substring(colon+3, line.length()-2).equals("Final")){
                        this.status = 1;
                    }
                }
            }
        }
        in.close();
    }

    @Override
    public void update() throws IOException {
        URL leagueTeams = new URL(this.link);
        BufferedReader in = new BufferedReader(new InputStreamReader(leagueTeams.openStream()));
        String inputLine;
        boolean start = false;
        while ((inputLine = in.readLine()) != null) {
            String[] lines = inputLine.split("\n");
            for (String line : lines) {
                if (line.contains("currentPlay")){
                    start = true;
                }
                else if (line.contains("away") && start){
                    int colon = line.indexOf(":");
                    awayScore = Integer.parseInt(line.substring(colon+2, line.length()-1));
                }
                else if (line.contains("home") && start){
                    int colon = line.indexOf(":");
                    homeScore = Integer.parseInt(line.substring(colon+2));
                    start = false;
                }
                else if (line.contains("ordinalNum") && start){
                    int colon = line.indexOf(":");
                    period = line.substring(colon+3, line.length()-2);
                }
                else if (line.contains("periodTimeRemaining") && start){
                    int colon = line.indexOf(":");
                    periodTime = line.substring(colon+3, line.length()-2);
                }
                else if (line.contains("abstractGameState")) {
                    int colon = line.indexOf(":");
                    if (line.substring(colon+3, line.length()-2).equals("Preview")){
                        this.status = -1;
                    }
                    if (line.substring(colon+3, line.length()-2).equals("Live")){
                        this.status = 0;
                    }
                    if (line.substring(colon+3, line.length()-2).equals("Final")){
                        this.status = 1;
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
            return awayTeam + ": " + awayScore + " @ " + homeTeam + ": " + homeScore + " with " + periodTime +
                    " remaining in the " + period;
        }
        else if (status == -1){
            return awayTeam + " @ " + homeTeam + " on " + month + "/" + day + " at " + time;
        }
        else {
            return "FINAL: " + awayTeam + ": " + awayScore + " @ " + homeTeam + ": " + homeScore + " on " + month + "/" + day;
        }
    }

    @Override
    public String toString(){
        return awayTeam + " @ " + homeTeam + " on " + month + "/" + day;
    }

    @Override
    public String getGameTime(){
        if (periodTime.equals("00:00")){
            return "End of " + period;
        }
        else{
            return periodTime + " in the " + period;
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
