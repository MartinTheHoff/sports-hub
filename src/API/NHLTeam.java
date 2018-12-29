package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NHLTeam implements Team{
    /** full String name of the team */
    private String name;
    /** link to page of the team */
    private String link;
    /** Teams 3 letter abbreviation */
    private String abbreviation;
    /** String of shorter team name */
    private String teamName;
    /** Name of teams city/location */
    private String locationName;
    /** the next or current Game the team will play */
    private Game nextGame;
    /** the last Game the team played */
    private Game lastGame;
    private int id;
    private LeagueType type = LeagueType.NHL;

    public NHLTeam(String link) throws IOException{
        this.link = link;
        id = -1;
        URL leagueTeams = new URL(link);
        BufferedReader in = new BufferedReader(new InputStreamReader(leagueTeams.openStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            String[] lines = inputLine.split("\n");
            for (String line : lines) {
                if (line.contains("id") && id == -1) {
                    int colon = line.indexOf(":");
                    id = Integer.parseInt(line.substring(colon+2, line.length()-1));
                }
                else if (line.contains("name") && name == null) {
                    int colon = line.indexOf(":");
                    name = line.substring(colon+3, line.length()-2);
                }
                else if (line.contains("abbreviation") && abbreviation == null) {
                    int colon = line.indexOf(":");
                    abbreviation = line.substring(colon+3, line.length()-2);
                }
                else if (line.contains("teamName") && teamName == null){
                    int colon = line.indexOf(":");
                    teamName = line.substring(colon+3, line.length()-2);
                }
                else if (line.contains("locationName") && locationName == null){
                    int colon = line.indexOf(":");
                    locationName = line.substring(colon+3, line.length()-2);
                }
            }
        }
        in.close();

        nextGame = fetchGame(link + "/?expand=team.schedule.next");
        lastGame = fetchGame(link + "/?expand=team.schedule.previous");
        if (nextGame == null){
            nextGame = lastGame;
        }
    }

    /**
     * Fetches and returns a game
     * @param teamLink link to get game from
     * @return Game
     * @throws IOException
     */
    public Game fetchGame(String teamLink) throws IOException{
        String gameLink = "";
        URL leagueTeams = new URL(teamLink);
        BufferedReader in = new BufferedReader(new InputStreamReader(leagueTeams.openStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            String[] lines = inputLine.split("\n");
            for (String line : lines) {
                if (line.contains("/feed/live") && (nextGame == null || lastGame == null)){
                    int colon = line.indexOf(":");
                    gameLink = "https://statsapi.web.nhl.com" + line.substring(colon+3, line.length()-2);
                }
            }
        }
        in.close();
        if (!gameLink.contains("com")){
            return new NHLGame("https://statsapi.web.nhl.com/api/v1/game/2018020339/feed/live");
        }
        return new NHLGame(gameLink);
    }

    public Game getNextGame(){
        return this.nextGame;
    }
    public Game getLastGame(){
        return this.lastGame;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }

    @Override
    public int getID(){
        return id;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public LeagueType getType(){ return type;}
}
