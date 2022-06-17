package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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

  public PlayerState getMe(ArenaUpdate arenaUpdate) {
    return arenaUpdate.arena.state.get(arenaUpdate._links.self.href);
  }

  public boolean isWithinRange(PlayerState me, PlayerState ps) {
    Integer direction = isMeFacingPlayer(me, ps);
    switch (direction) {
      case 1: return (me.x + 3) <= ps.x;
      case 2: return (me.y + 3) <= ps.y;
      case 3: return (me.x - 3) <= ps.x;
      case 4: return (me.y - 3) <= ps.y;
      case 0: return false;
    }
    return false;
  }

  public PlayerState getClosestPlayer(ArenaUpdate arenaUpdate) {
    Set<String> playerLinks = arenaUpdate.arena.state.keySet();

    PlayerState closestPlayer  = null;
    String minDistanceLink = "";
    Double minDistance = Double.MAX_VALUE;
    for (String playerLink: playerLinks) {
      PlayerState ps = arenaUpdate.arena.state.get(playerLink);
      PlayerState me = getMe(arenaUpdate);
      Double distance = calculateDistanceBetweenPoints(me.x, me.y, ps.x, ps.y);
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

  public Integer isMeFacingPlayer(PlayerState me, PlayerState ps) {
    if (me.direction.equalsIgnoreCase("e")) {
      if (me.y == ps.y && me.x < ps.x) {
        return 1;
      }
    } else if (me.direction.equalsIgnoreCase("s")) {
      if (me.x == ps.x && me.y > ps.y) {
        return 2;
      }
    } else if (me.direction.equalsIgnoreCase("w")) {
      if (me.y == ps.y && me.x > ps.x) {
        return 3;
      }
    } else if (me.direction.equalsIgnoreCase("n")) {
      if (me.x == ps.x && me.y < ps.y) {
        return 4;
      }
    }

    return 0;
  }

  @PostMapping("/**")
  public String index(@RequestBody ArenaUpdate arenaUpdate) {
    try {
      System.out.println(arenaUpdate);

//      String[] commands = new String[]{"F", "R", "L", "T"};
      String[] direction = new String[]{"E", "S", "W", "N"};

      String selfLink = arenaUpdate._links.self.href;
      PlayerState selfState = this.getSelfState(arenaUpdate);
      List<Integer> dims = arenaUpdate.arena.dims;

      String currentDirection = selfState.direction;

      PlayerState closestPlayer = getClosestPlayer(arenaUpdate);
      PlayerState me = getMe(arenaUpdate);

      Boolean inRange = isWithinRange(getMe(arenaUpdate), closestPlayer);

      if (inRange) {
        return "T";
      } else {
        String[] commands = new String[]{"F", "R", "L"};
        Random ran = new Random();
        int r = ran.nextInt(3) + 0;
        return commands[r];
      }

//    int i = new Random().nextInt(4);
//    return commands[i];
    } catch (Exception e) {
      return "T";
    }
  }

  public PlayerState getSelfState(ArenaUpdate arenaUpdate) {
    String selfLink = arenaUpdate._links.self.href;
    return arenaUpdate.arena.state.get(selfLink);
  }

}

