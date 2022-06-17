package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@RestController
public class Application {

  static class Self {
    public String href;
  }

  static class Links {
    public Self self;
  }

  static class PlayerState {
    public Integer x;
    public Integer y;
    public String direction;
    public Boolean wasHit;
    public Integer score;
  }

  static class Arena {
    public List<Integer> dims;
    public Map<String, PlayerState> state;
  }

  static class ArenaUpdate {
    public Links _links;
    public Arena arena;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.initDirectFieldAccess();
  }

  @GetMapping("/")
  public String index() {
    return "Let the battle begin!";
  }

  @PostMapping("/**")
  public String index(@RequestBody ArenaUpdate arenaUpdate) {
    System.out.println(arenaUpdate);

    String[] commands = new String[]{"F", "R", "L", "T"};
    String[] direction = new String[]{"E", "S", "W", "N"};

    String selfLink = arenaUpdate._links.self.href;
    PlayerState selfState = this.getSelfState(arenaUpdate);
    List<Integer> dims = arenaUpdate.arena.dims;

    String currentDirection = selfState.direction;

    Map<String, PlayerState> allPlayerState = arenaUpdate.arena.state;

    switch (currentDirection.toUpperCase()) {
      case "E":
        for (Map.Entry<String, PlayerState> map : allPlayerState.entrySet()) {
          PlayerState playerState = map.getValue();
          if (!playerState.y.equals(selfState.y)) {
            return "R";
          }
          if (playerState.x.compareTo(selfState.x + 3) >= 0) {
            return "T";
          } else if (playerState.x.compareTo(selfState.x) > 0){
            return "F";
          }
        }

      case "W":
        for (Map.Entry<String, PlayerState> map : allPlayerState.entrySet()) {
          PlayerState playerState = map.getValue();
          if (!playerState.y.equals(selfState.y)) {
            return "R";
          }
          if (playerState.x.compareTo(selfState.x -3) <= 0) {
            return "T";
          } else if (playerState.x.compareTo(selfState.x) < 0){
            return "F";
          }
        }
        return "R";
      case "N":
        for (Map.Entry<String, PlayerState> map : allPlayerState.entrySet()) {
          PlayerState playerState = map.getValue();
          if (!playerState.x.equals(selfState.x)) {
            return "R";
          }
          if (playerState.y.compareTo(selfState.y - 3) <= 0) {
            return "T";
          } else if (playerState.y.compareTo(selfState.y) < 0){
            return "F";
          }
        }
        return "R";
      case "S":
        for (Map.Entry<String, PlayerState> map : allPlayerState.entrySet()) {
          PlayerState playerState = map.getValue();
          if (!playerState.x.equals(selfState.x)) {
            return "R";
          }
          if (playerState.y.compareTo(selfState.y + 3) >= 0) {
            return "T";
          } else if (playerState.y.compareTo(selfState.y) > 0){
            return "F";
          }
        }
        return "R";
    }


    int i = new Random().nextInt(4);
    return commands[i];
  }

  public PlayerState getSelfState(ArenaUpdate arenaUpdate) {
    String selfLink = arenaUpdate._links.self.href;
    return arenaUpdate.arena.state.get(selfLink);
  }

}

