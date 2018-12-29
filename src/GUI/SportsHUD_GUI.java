package GUI;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import API.*;
import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.*;
import java.util.*;

/*
    TODO:

    Better GUI

    Next Game and Previous Game
        Done for NHL

    Auto Refresh
 */
public class SportsHUD_GUI extends Application implements Observer {

    private SportsHUD model;
    private ZonedDateTime localDate;
    private VBox favGames;
    private VBox nhlGames;
    private VBox premGames;

    private ScrollPane scrollNHLGames;
    private ScrollPane scrollPremGames;

    @Override
    public void init(){
        model = new SportsHUD();
        localDate = ZonedDateTime.now();
        this.model.addObserver(this);

        favGames = new VBox();
        nhlGames = new VBox();
        premGames = new VBox();

        scrollNHLGames = new ScrollPane();
        scrollPremGames = new ScrollPane();
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Sports HUD");

        Button addFavorites = new Button("+Favorites");
        EventHandler<ActionEvent> addFavoritesWindow = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Stage favsStage = new Stage();
                GridPane favsPane = addFavoriteTeams();
                Scene favsScene = new Scene(favsPane);
                favsStage.setScene(favsScene);
                favsStage.show();
            }
        };
        addFavorites.setOnAction(addFavoritesWindow);

        Button refresh = new Button("Refresh");
        EventHandler<ActionEvent> refreshStuff = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                model.updateAllGames();
            }
        };
        refresh.setOnAction(refreshStuff);

        favGames = refresh(model.getFavGames());
        nhlGames = refresh(model.getAllNHLGames());
        premGames = refresh(model.getAllPREMGames());

        Text favText = new Text("Favorites");
        favText.setFill(Color.DARKRED);
        favText.setFont(Font.font(20));

        Line betweenLine = new Line(20,0,300,0);
        betweenLine.setStroke(Color.DARKRED);

        Line betweenLine2 = new Line(20,0,300,0);
        betweenLine2.setStroke(Color.DARKRED);

        Line betweenLine3 = new Line(20,0,300,0);
        betweenLine2.setStroke(Color.DARKRED);

        Text nhlText = new Text("NHL");
        nhlText.setFill(Color.DARKRED);
        nhlText.setFont(Font.font(20));

        Text premText = new Text("Premier League");
        premText.setFill(Color.DARKRED);
        premText.setFont(Font.font(20));

        scrollNHLGames.setContent(nhlGames);
        scrollNHLGames.setPrefViewportHeight(300);
        scrollNHLGames.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //scrollNHLGames.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPremGames.setContent(premGames);
        scrollPremGames.setPrefViewportHeight(300);
        scrollPremGames.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //scrollPremGames.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox main = new VBox(10,addFavorites, favText, betweenLine, favGames, nhlText, betweenLine2, scrollNHLGames, premText, betweenLine3, scrollPremGames,refresh);

        Scene mainScene = new Scene(main);
        primaryStage.setScene(mainScene);
        primaryStage.show();

       /*new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                model.updateAllGames();
                System.out.println("Refreshed");
            }
        }, 20000, 20000);*/
    }

    private VBox refresh(ArrayList<Game> games){
        VBox out = new VBox(5);
        for (Game game: games){
            out.getChildren().add(gamePane(game));
            out.getChildren().add(new Line(20,0,300,0));
        }
        return out;
    }

    @Override
    public void update( Observable t, Object o ) {

        assert t == this.model: "Update from non-model Observable";

        favGames = refresh(model.getFavGames());
        nhlGames = refresh(model.getAllNHLGames());
        premGames = refresh(model.getAllPREMGames());

    }

    private Pane gamePane(Game game){
        GridPane gamePane = new GridPane();
        int offset = Integer.parseInt(localDate.getOffset().toString().substring(0,localDate.getOffset().toString().length()-3));

        Text homeTeam = new Text();
        Text awayTeam = new Text();
        Text homeScore = new Text();
        Text awayScore = new Text();
        Text time = new Text();

        if (game.getType() == Game.GameType.NHL){
            homeTeam.setText(model.getLeague(League.LeagueType.NHL).getTeam(game.getHomeTeam()).getName());
            awayTeam.setText(model.getLeague(League.LeagueType.NHL).getTeam(game.getAwayTeam()).getName());
        }
        if (game.getType() == Game.GameType.PREM){
            homeTeam.setText(model.getLeague(League.LeagueType.PREM).getTeam(game.getHomeTeam()).getName());
            awayTeam.setText(model.getLeague(League.LeagueType.PREM).getTeam(game.getAwayTeam()).getName());
        }
        if (game.getStatus() == -1) {
            String dateTime;
            LocalTime gameTime = game.getTime();
            gameTime = gameTime.plusHours(offset);
            if (gameTime.getHour() > 12){
                gameTime = gameTime.minusHours(12);
                dateTime = gameTime.toString() + " PM";
            } else {
                dateTime = gameTime.toString() + " AM";
            }
            if (game.getMonth() != localDate.getMonth().getValue() || game.getDay() != localDate.getDayOfMonth()) {
                String temp = dateTime;
                dateTime = game.getMonth() + "/" + game.getDay() + " @ " + temp;
            }
            time.setText(dateTime);
        }
        else if (game.getStatus() >= 0) {
            game.getScore();
            homeScore.setText(String.valueOf(game.getHomeScore()));
            awayScore.setText(String.valueOf(game.getAwayScore()));
            time.setText(game.getGameTime());
            if (game.getStatus() == 1){
                time.setText("FINAL");
            }
        }
        gamePane.setHgap(10);
        gamePane.setVgap(2);

        gamePane.add(homeTeam, 0 , 2);
        gamePane.add(awayTeam, 0 ,0);
        gamePane.add(homeScore, 1 ,2);
        gamePane.add(awayScore, 1 ,0);
        gamePane.add(time, 2, 1);

        return gamePane;
    }

    private GridPane addFavoriteTeams(){
        GridPane out = new GridPane();
        out.setVgap(5);
        //ComboBox cbTeams = new ComboBox();
        ComboBox[] temp = { null };
        temp[0] = new ComboBox();
        out.add(temp[0], 1,1);

        ComboBox cbLeagues = new ComboBox(FXCollections.observableArrayList(model.getListOfLeagues()));
        cbLeagues.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            if (newValue.equals(model.getLeague(League.LeagueType.NHL))) {
                temp[0] = new ComboBox(FXCollections.observableArrayList(model.getLeague(League.LeagueType.NHL).getTeams()));
                out.add(temp[0],1,1);
            }
            if (newValue.equals(model.getLeague(League.LeagueType.PREM))) {
                temp[0] = new ComboBox(FXCollections.observableArrayList(model.getLeague(League.LeagueType.PREM).getTeams()));
                out.add(temp[0],1,1);
            }
            temp[0].getSelectionModel().selectedItemProperty().addListener( (optionsT, oldValueT, newValueT) -> {
                model.addFavorite((Team) newValueT);
                out.add(new Text(newValueT + " added to your favorties"), 1,2,2,1);
            });
        });

        //cbTeams = temp[0];

        out.add(new Text("League: "), 0,0);
        out.add(cbLeagues,1,0);
        out.add(new Text("Team: "), 0,1);
        return out;
    }

}
