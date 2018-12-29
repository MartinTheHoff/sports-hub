package API;

public class PremierLeagueTeam implements Team {
    /** API.Team's unique ID */
    private int id;
    /** full String name of the team */
    private String name;
    /** Teams 3 letter abbreviation */
    private String abbreviation;
    /** the next or current API.Game the team will play */
    private PremierLeagueGame nextGame;
    private LeagueType type = LeagueType.PREM;

    public PremierLeagueTeam(String line){
        if (line.contains("id")) {
            int start = line.indexOf("id");
            int end = line.indexOf(",");
            this.id = Integer.parseInt(line.substring(start+4, end));
        }
        if (line.contains("short_name")) {
            int start = line.indexOf("short_name");
            int end = line.indexOf(",", start);
            this.abbreviation = line.substring(start+13, end-1);
        }
        if (line.contains("name")) {
            int start = line.indexOf("name");
            int end = line.indexOf(",", start);
            this.name = line.substring(start+7, end-1);
        }
        if (line.contains("next_event_fixture")) {
            nextGame = new PremierLeagueGame(line, id);
        }
    }

    public PremierLeagueGame getNextGame(){
        return this.nextGame;
    }

    @Override
    public String toString(){
        return name;
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
