package API;

import java.io.IOException;
import java.time.LocalTime;

public interface Game {
    int getHomeTeam();
    int getAwayTeam();
    String getScore();
    GameType getType();
    int getHomeScore();
    int getAwayScore();
    int getStatus();
    int getMonth();
    int getDay();
    LocalTime getTime();
    String getGameTime();
    void update() throws IOException;
    enum GameType{
        NHL,
        PREM,
    }
}
