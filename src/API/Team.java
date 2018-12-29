package API;

public interface Team {
    Game getNextGame();
    int getID();
    String getName();
    LeagueType getType();
    enum LeagueType{
        NHL,
        PREM,
    }
}
