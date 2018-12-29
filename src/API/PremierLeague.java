package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class PremierLeague implements League {
    /** ArrayList of all teams in the API.NHL */
    private ArrayList<PremierLeagueTeam> teams;

    private LeagueType type = LeagueType.PREM;

    public PremierLeague() throws IOException {
        teams = new ArrayList<>();
        URL leagueTeams = new URL("https://fantasy.premierleague.com/drf/teams");
        BufferedReader in = new BufferedReader(new InputStreamReader(leagueTeams.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            String[] lines = inputLine.split("[}],[{]");
            for (String line : lines) {
                PremierLeagueTeam team = new PremierLeagueTeam(line);
                teams.add(team);
            }
        }
        in.close();
    }

    /**
     * Gets an ArrayList of all Teams in the API.NHL
     * @return ArrayList of all Teams in the API.NHL
     */
    @Override
    public ArrayList<PremierLeagueTeam> getTeams(){
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
    public String toString(){
        return "Premier League";
    }

    @Override
    public LeagueType getType(){ return type;}

    @Override
    public boolean equals(Object o){
        if (o instanceof League){
            return type == ((League) o).getType();
        }
        return false;
    }

    public static void main(String[] args) {

        try {
            PremierLeague league = new PremierLeague();
            ArrayList<PremierLeagueTeam> teams = league.getTeams();
            for (PremierLeagueTeam team: teams){
                System.out.println(team);
                //System.out.println(team.getNextGame().toString());
                System.out.println(team.getNextGame().getScore());
            }

            System.out.println();

            System.out.println(league.getTeam("Liverpool").getNextGame().getScore());

            // System.out.println();

            /*
            util.Favorites fav = new util.Favorites();
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
