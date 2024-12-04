package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.SnackType;
import de.hsrm.mi.swt.snackman.services.MapService;
import de.hsrm.mi.swt.snackman.services.ReadMazeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class SnackmanTest {

    private SnackMan snackMan;
    private Square square;

    @BeforeEach
    public void setUp() {

        snackMan = new SnackMan(new MapService(new ReadMazeService()));

        Snack snack = new Snack(SnackType.APPLE);
        square = new Square(snack, 0, 0);
    }

    @Test
    void testConsumeSnack() {
        assertEquals(0, snackMan.getCurrentCalories(), "Initial calories should be 0.");

        snackMan.consumeSnackOnSquare(square);
        assertEquals(SnackType.APPLE.getCalories(), snackMan.getCurrentCalories(), "After consuming an Apple the calories of snackman should increase.");
        assertNull(square.getSnack(), "After consuming the snack, the square should no longer have a snack.");
    }

    @Test
    void testConsumeMaximumSnacks(){
        Snack apple = new Snack(SnackType.APPLE);
        Square appleSquare1 = new Square(apple, 0, 0);
        Square appleSquare2 = new Square(apple, 0, 1);
        Square appleSquare3 = new Square(apple, 0, 2);
        Square appleSquare4 = new Square(apple, 0, 3);
        Square appleSquare5 = new Square(apple, 0, 4);

        assertEquals(0, snackMan.getCurrentCalories(), "Initial calories should be 0.");

        // adding Snacks to Square -> MAXCALORIES could be checked

        snackMan.consumeSnackOnSquare(appleSquare1);
        snackMan.consumeSnackOnSquare(appleSquare2);
        snackMan.consumeSnackOnSquare(appleSquare3);
        snackMan.consumeSnackOnSquare(appleSquare4);
        snackMan.consumeSnackOnSquare(appleSquare5);

        assertEquals(snackMan.getMAXCALORIES(), snackMan.getCurrentCalories(), "After consuming 5 apples the calories should be capped at 3000.");

        assertNull(appleSquare1.getSnack(), "After consuming the snacks, the square should no longer have a snack.");
        assertNull(appleSquare2.getSnack(), "After consuming the snacks, the square should no longer have a snack.");
        assertNull(appleSquare3.getSnack(), "After consuming the snacks, the square should no longer have a snack.");
        assertNull(appleSquare4.getSnack(), "After consuming the snacks, the square should no longer have a snack.");
        assertNull(appleSquare5.getSnack(), "After consuming the snacks, the square should no longer have a snack.");





    }
}
