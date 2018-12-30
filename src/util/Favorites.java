package util;

import API.Team;
import java.io.*;
import java.util.ArrayList;

public class Favorites {
    private ArrayList<Team> favoriteTeams;

    public Favorites(){
        favoriteTeams = new ArrayList<>();
    }

    public void addFavorite(Team favTeam){
        favoriteTeams.add(favTeam);
        try{
            String save = getFile();
            for (Team team: favoriteTeams){
                save += team.getType() + "|";
                save += team.toString() + "~";
            }
            try {
                writeFile("Favorites/Favorites.txt", save);
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        } catch (IOException e){

        }

    }

    public void writeFile(String fileLocation, String text) throws IOException
    {
        File file = new File(fileLocation);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(text);
        out.close();
    }

    public String getFile() throws IOException{
        return getFile("Favorites/Favorites.txt");
    }

    public String getFile(String fileLocation) throws IOException{
        File save = new File(fileLocation);
        BufferedReader in = new BufferedReader(new FileReader(save));
        return in.readLine();
    }

    public String[] readFile() throws IOException{
        return readFile("Favorites/Favorites.txt");
    }

    public String[] readFile(String fileLocation) throws IOException{
        File save = new File(fileLocation);
        BufferedReader in = new BufferedReader(new FileReader(save));
        String inputLine;

        String[] lines = new String[0];
        while ((inputLine = in.readLine()) != null) {
            lines = inputLine.split("~");
        }
        in.close();
        return lines;
    }
}
