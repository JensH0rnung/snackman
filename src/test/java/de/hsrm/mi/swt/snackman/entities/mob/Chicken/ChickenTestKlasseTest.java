package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters.TalaChickenBehavior;

public class ChickenTestKlasseTest {
    
    @Test
    public void testTalaChickenMovement(){
        StrategyChicken chicken = new StrategyChicken(new TalaChickenBehavior());

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W", "L", "W", "L", "0");
        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");

    }

    // @Test //rausgenommen, weil TalaChicken ein größeres Sichtfeld braucht, um Snackman zu finden
    // public void testTalaChickenFollowsSnackman(){
    //     StrategyChicken chicken = new StrategyChicken(new TalaChickenBehavior());

    //     List<String> visibleEnvironment = List.of("W", "SM", "W", "L", "W", "L", "W", "L", "0");
    //     List<String> result = chicken.act(visibleEnvironment);
    //     int chosenIndex = Integer.parseInt(result.get(result.size() - 1));
    //     assertEquals(" ", result.get(chosenIndex), " The Chicken should move to SnackMan ('SM') matching its new direction");

    //     List<String> visibleEnvironment2 = List.of("W", "L", "W", "L", "W", "SM", "W", "L", "0");
    //     List<String> result2 = chicken.act(visibleEnvironment2);
    //     int chosenIndex2 = Integer.parseInt(result2.get(result2.size() - 1));
    //     assertEquals(" ", result.get(chosenIndex2), " The Chicken should move to SnackMan ('SM') matching its new direction");
    // }

    @Test
    public void testTalaCkickenFollowsSnackmanWithBigDistance(){
        StrategyChicken chicken = new StrategyChicken(new TalaChickenBehavior());

        List<String> visibleEnvironment = List.of("W", "W", "W", "W", "L", "W", "W", "W", "W",
                                                    "W", "SM", "L", "L", "W", "L", "W", "L", "W",
                                                    "W", "W", "L", "W", "W", "L", "L", "L", "W",
                                                    "L", "L", "L", "L", "W", "L", "W", "W", "W",
                                                    "W", "L", "W", "SM", "H", "L", "W", "L", "W", //H is Chicken
                                                    "W", "L", "W", "L", "W", "L", "W", "L", "L",
                                                    "W", "L", "W", "L", "W", "W", "W", "L", "W",
                                                    "W", "W", "W", "L", "L", "W", "W", "W", "W",
                                                    "W", "W", "W", "W", "L", "W", "W", "W", "W");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenIndex = Integer.parseInt(result.get(result.size() - 1));
        assertEquals("", result.get(chosenIndex), "Chicken didnt move to Snackman");

    }

    @Test
    public void testTalaChickenFollowsSnackmanButCannotReach(){
    //weil z.B. Wäde im Weg sind
        StrategyChicken chicken = new StrategyChicken(new TalaChickenBehavior());

        List<String> visibleEnvironment = List.of("W", "W", "W", "W", "W", "W", "W", "W", "W",
                                                "W", "SM", "W", "L", "W", "L", "L", "L", "W",
                                                "W", "W", "W", "L", "W", "L", "W", "L", "W",
                                                "W", "L", "W", "L", "W", "L", "W", "W", "W",
                                                "W", "L", "W", "L", "H", "L", "L", "L", "L",
                                                "W", "L", "W", "L", "W", "L", "W", "L", "L",
                                                "W", "L", "W", "L", "W", "L", "W", "L", "W",
                                                "W", "L", "W", "L", "L", "L", "W", "L", "W",
                                                "W", "L", "W", "L", "W", "W", "W", "L", "W");

        List<String> result = chicken.act(visibleEnvironment);
        boolean istFreierIndexVorhanden = result.contains(" ");
        assertEquals(false, istFreierIndexVorhanden, "Empty element, but Snackman is not reachable : "+ result);

    }

}
