package API;

import util.Favorites;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class NHL implements League {
    /** ArrayList of all teams in the API.NHL */
    private ArrayList<NHLTeam> teams;

    private LeagueType type = LeagueType.NHL;

    /**
     * creates the API.NHL and calls API.NHLTeam() and adds all teams to an array list of NHLTeams
     */
    public NHL() throws IOException{
        teams = new ArrayList<>();
        URL leagueTeams = new URL("https://statsapi.web.nhl.com/api/v1/teams");
        BufferedReader in = new BufferedReader(new InputStreamReader(leagueTeams.openStream()));
        String inputLine;
        String link = "";
        while ((inputLine = in.readLine()) != null) {
            String[] lines = inputLine.split("\n");
            for (String line : lines) {
                if (line.contains("link") && link.equals("")) {
                    int colon = line.indexOf(":");
                    link = line.substring(colon+3, line.length()-2);
                }
                else if (line.contains("active")){
                    NHLTeam team = new NHLTeam("https://statsapi.web.nhl.com" + link);
                    teams.add(team);
                    link = "";
                }
            }
        }
        in.close();
    }

    /**
     * Gets an ArrayList of all Teams in the API.NHL
     * @return ArrayList of all Teams in the API.NHL
     */
    @Override
    public ArrayList<NHLTeam> getTeams(){
        return teams;
    }

    /**
     * Gets a API.Team based on the full team name
     * @param name String of full team name
     * @return API.Team with name from input
     */
    @Override
    public Team getTeam(String name){
        for (Team out: teams){
            if (out.toString().equals(name)){
                return out;
            }
        }
        return null;
    }
    @Override
    public Team getTeam(int id){
        for (Team out: teams){
            if (out.getID() == id){
                return out;
            }
        }
        return null;
    }

    @Override
    public LeagueType getType(){ return type;}

    @Override
    public String toString(){
        return "NHL";
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof League){
            return type == ((League) o).getType();
        }
        return false;
    }

    /**
     * Main method to test
     * @param args
     */
    public static void main(String[] args) {
        try {
            NHL league = new NHL();
            ArrayList<NHLTeam> teams = league.getTeams();
            for (API.NHLTeam team: teams){
                System.out.println(team.toString());
                System.out.println(team.getNextGame().toString());
            }

            //System.out.println();

            try{
                league.getTeam("San Jose Sharks").getNextGame().update();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            System.out.println(league.getTeam("San Jose Sharks").getNextGame().getScore());

            // System.out.println();

            /*
            Favorites fav = new Favorites();
            fav.addFavorite(league.getTeam("San Jose Sharks"));
            fav.addFavorite(league.getTeam("Nashville Predators"));

            String[] favTeams;
            try {
                favTeams = fav.readFile();
                for (String team: favTeams){
                    System.out.println(team);
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            */
        } catch (IOException e){
            System.out.println(e.getMessage());
        }


    }
}
