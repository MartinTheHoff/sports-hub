package API;

import java.util.ArrayList;

public interface League {
    /** Method to get all teams in the league from the API */
    ArrayList getTeams();
    /** Method to get a API.Team in the league. */
    Team getTeam(String teamName);
    Team getTeam(int id);
    LeagueType getType();
    enum LeagueType{
        NHL,
        PREM,
    }
}
