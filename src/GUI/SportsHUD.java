package GUI;

import API.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;

import util.Favorites;

import java.io.IOException;
import java.time.ZonedDateTime;

public class SportsHUD extends Observable {
    private ArrayList<League> listOfLeagues;
    private NHL nhl;
    private PremierLeague premierLeague;

    private Favorites favorites;
    private ArrayList<Team> favTeams;
    private ArrayList<Game> favGames;

    private ArrayList<Game> allNHLGames;
    private ArrayList<Game> allPREMGames;

    private ZonedDateTime localDate;

    public SportsHUD(){
        try {
            localDate = ZonedDateTime.now();
            allNHLGames = new ArrayList<>();
            allPREMGames = new ArrayList<>();
            favGames = new ArrayList<>();
            listOfLeagues = new ArrayList<>();
            nhl = new NHL();
            listOfLeagues.add(nhl);
            premierLeague = new PremierLeague();
            listOfLeagues.add(premierLeague);
            favorites = new Favorites();
            favTeams = new ArrayList<>();

            getFavTeams();
            getAllNHL();
            getAllPREM();
        } catch (IOException e){
            System.out.println("init() " + e.getMessage());
        }
    }

    public ArrayList<Game> getFavGames(){
        favGames.sort(Comparator.comparing(Game::getTime));
        return favGames;
    }

    public ArrayList<Game> getAllNHLGames(){
        favGames.sort(Comparator.comparing(Game::getTime));
        return allNHLGames;
    }

    public ArrayList<Game> getAllPREMGames(){
        favGames.sort(Comparator.comparing(Game::getTime));
        return allPREMGames;
    }

    public void updateAllGames(){
        for (Game game: allPREMGames){
            try {
                game.update();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        for (Game game: allNHLGames){
            try {
                game.update();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        for (Game game: favGames){
            try {
                game.update();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        super.notifyObservers();
    }

    private void getFavTeams(){
        try {
            String[] temp = favorites.readFile();
            for (String line: temp){
                String[] word = line.split("[|]");
                if (word[0].equals("NHL")){
                    favTeams.add(nhl.getTeam(word[1]));
                }
                if (word[0].equals("PREM")){
                    favTeams.add(premierLeague.getTeam(word[1]));
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        for (Team team: favTeams){
            favGames.add(team.getNextGame());
        }
    }

    private void getAllNHL(){
        for (Team team: nhl.getTeams()){
            Game game = team.getNextGame();
            if (game.getDay() == localDate.getDayOfMonth() && !(favTeams.contains(team)) &&
                    !(favTeams.contains(nhl.getTeam(game.getAwayTeam()))) && !(allNHLGames.contains(game))){
                allNHLGames.add(game);
            }
        }
    }

    private void getAllPREM(){
        for (Team team: premierLeague.getTeams()){
            Game game = team.getNextGame();
            if (game.getDay() <= localDate.getDayOfMonth() && !(favTeams.contains(team)) &&
                    !(favTeams.contains(premierLeague.getTeam(game.getAwayTeam()))) && !(allPREMGames.contains(game))){
                allPREMGames.add(game);
            }
        }
    }

    public ArrayList<League> getListOfLeagues() {
        return listOfLeagues;
    }

    public League getLeague(League.LeagueType type){
        if (type == League.LeagueType.NHL){
            return nhl;
        }
        if (type == League.LeagueType.PREM){
            return premierLeague;
        }
        return nhl;
    }

    public void addFavorite(Team team){
        favTeams.add(team);
    }
}
