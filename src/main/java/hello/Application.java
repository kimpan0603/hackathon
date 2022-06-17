package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

  public double calculateDistanceBetweenPoints(
          double x1,
          double y1,
          double x2,
          double y2) {
    return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
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

  public PlayerState me(ArenaUpdate arenaUpdate) {
    return arenaUpdate.arena.state.get(arenaUpdate._links);
  }

  public PlayerState closestPlayer(ArenaUpdate arenaUpdate) {
    Set<String> playerLinks = arenaUpdate.arena.state.keySet();

    PlayerState closestPlayer  = null;
    String minDistanceLink = "";
    Double minDistance = Double.MAX_VALUE;
    for (String playerLink: playerLinks) {
      PlayerState ps = arenaUpdate.arena.state.get(playerLink);
      Double distance = calculateDistanceBetweenPoints(me(arenaUpdate).x, me(arenaUpdate).y, ps.x, ps.y);
      if (Double.compare(minDistance, distance) > 1) {
        minDistance = distance;
        closestPlayer = ps;
      }
      if (closestPlayer == null) { // just in case
        closestPlayer = ps;
      }
    }

    return closestPlayer;
  }

  public boolean isMeFacingPlayer(PlayerState me, PlayerState ps) {
    if (me.direction.equalsIgnoreCase("e")) {
      if (me.y == ps.y && me.x < ps.x) {
        return true;
      }
    } else if (me.direction.equalsIgnoreCase("s")) {
      if (me.x == ps.x && me.y > ps.y) {
        return true;
      }
    } else if (me.direction.equalsIgnoreCase("w")) {
      if (me.y == ps.y && me.x > ps.x) {
        return true;
      }
    } else if (me.direction.equalsIgnoreCase("n")) {
      if (me.x == ps.x && me.y < ps.y) {
        return true;
      }
    }

    return false;
  }

  @PostMapping("/**")
  public String index(@RequestBody ArenaUpdate arenaUpdate) {
    System.out.println(arenaUpdate);
    String[] commands = new String[]{"F", "R", "L", "T"};
    int i = new Random().nextInt(4);
    return commands[i];
  }

}

