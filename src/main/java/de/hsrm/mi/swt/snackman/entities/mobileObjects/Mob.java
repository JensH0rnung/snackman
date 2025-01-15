package de.hsrm.mi.swt.snackman.entities.mobileObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hsrm.mi.swt.snackman.entities.map.GameMap;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.MapObjectType;

/**
 * A mobile object with the ability to move its position
 */
public abstract class Mob {
    private static long idCounter = 0;
    protected long id;
    private Vector3d position;
    private double radius;
    private Quaterniond quat;
    private double speed;
    private Vector3d spawn;
    private Vector3d forward;
    private GameMap gameMap;

    /**
     * Base constructor for Map with spawn-location at center of Map
     *
     * @param gameMap GameMap
     * @param speed   speed of the mob
     * @param radius  size of the mob
     */
    public Mob(GameMap gameMap, double speed, double radius) {
        this.gameMap = gameMap;
        this.speed = speed;
        this.radius = radius;
        spawn = new Vector3d((gameMap.getGameMapSquares()[0].length / 2.0) * GameConfig.SQUARE_SIZE, GameConfig.SNACKMAN_GROUND_LEVEL,
                (gameMap.getGameMapSquares()[0].length / 2.0) * GameConfig.SQUARE_SIZE);
        position = new Vector3d(spawn);
        quat = new Quaterniond();
        setCurrentSquareWithIndex(position.x, position.z);
        setPositionWithIndexXZ(position.x, position.z);
        id = generateId();
        forward = new Vector3d(0, 0, -1);
    }

    public Mob() {
        position = new Vector3d();
        quat = new Quaterniond();
    }

    /**
     * Constructor for Mob with custom spawn point
     *
     * @param gameMap MapService of the map the mob is located on
     * @param speed   speed of the mob
     * @param radius  size of the mob
     * @param posX    x-spawn-position
     * @param posY    y-spawn-positon
     * @param posZ    z-spawn-position
     */
    public Mob(GameMap gameMap, double speed, double radius, double posX, double posY, double posZ) {
        this.gameMap = gameMap;
        this.speed = speed;
        this.radius = radius;

        spawn = new Vector3d(posX, posY, posZ);
        position = new Vector3d(spawn);
        quat = new Quaterniond();
        id = generateId();
    }

    protected synchronized static long generateId() {
        return idCounter++;
    }

    public double getPosX() {
        return position.x;
    }

    public void setPosX(double posX) {
        position.x = posX;
    }

    public double getPosY() {
        return position.y;
    }

    public void setPosY(double posY) {
        this.position.y = posY;
    }

    public double getPosZ() {
        return position.z;
    }

    public void setPosZ(double posZ) {
        this.position.z = posZ;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Quaterniond getRotationQuaternion() {
        return this.quat;
    }

    /**
     * Calculates the square-indices to set the currentSquare
     *
     * @param x x-position
     * @param z z-position
     */
    public void setCurrentSquareWithIndex(double x, double z) {
        setPositionWithIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z));
    }

    public void setPositionWithIndexXZ(double x, double z) {
        this.position.x = x;
        this.position.z = z;
    }

    /**
     * Moves the player based on inputs and passed time since last update (delta). Forward is relative to the rotation of the player and handled by a quaternion.
     * Result:
     * 0 = no blocked movement direction
     * 1 = blocked x movement direction
     * 2 = blocked z movement direction
     * 3 = blocked x and z movement directions
     *
     * @param f     input forward pressed?
     * @param b     input backward pressed?
     * @param l     input left pressed?
     * @param r     input right pressed?
     * @param delta time since last update
     */
    public void move(boolean f, boolean b, boolean l, boolean r, double delta, GameMap gameMap) {
        int result = 3;
        int moveDirZ = (f ? 1 : 0) - (b ? 1 : 0);
        int moveDirX = (r ? 1 : 0) - (l ? 1 : 0);

        Vector3d move = new Vector3d();

        if (f || b) {
            move.z -= moveDirZ;
        }
        if (l || r) {
            move.x += moveDirX;
        }

        move.rotate(quat);
        move.y = 0;
        if (!(move.x == 0 && move.z == 0))
            move.normalize();
        move.x = move.x * delta * speed;
        move.z = move.z * delta * speed;
        double xNew = position.x + move.x;
        double zNew = position.z + move.z;
        try {
            result = checkWallCollision(xNew, zNew, gameMap);
        } catch (IndexOutOfBoundsException e) {
            respawn();
            return;
        }
        switch (result) {
            case 0:
                position.x += move.x;
                position.z += move.z;
                break;
            case 1:
                position.z += move.z;
                break;
            case 2:
                position.x += move.x;
                break;
            case 3:
                return;
            default:
                break;
        }
        setPositionWithIndexXZ(position.x, position.z);
    }

    public Quaterniond getQuat() {
        return quat;
    }

    /**
     * respawns the mob at his spawn-location
     */
    public void respawn() {
        this.position.x = spawn.x;
        this.position.z = spawn.z;
        setCurrentSquareWithIndex(position.x, position.z);

    /*
    TODO unterschied zwischen snackman und geistern beachten in konstructor ändern!!
    für geister

    this.position.x = (mapService.getGameMap().getGameMap().length / 2) * GameConfig.SQUARE_SIZE;
        this.position.z = (mapService.getGameMap().getGameMap()[0].length / 2) * GameConfig.SQUARE_SIZE;
        setPositionWithIndexXZ(position.x, position.z);

     */

    }

    /**
     * firstly determines which walls are close and need to be checked for collision
     * secondly checks for collision with the walls (checks if target location is a wall and if player circle overlaps any walls)
     *
     * @param x target x-position
     * @param z target z-position
     * @returns the type of collision represented as an int
     * 0 = no collision
     * 1 = horizontal collision
     * 2 = vertical collision
     * 3 = both / diagonal collision / corner
     */
    public int checkWallCollision(double x, double z, GameMap gameMap) throws IndexOutOfBoundsException {
        if (gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z)).getType() == MapObjectType.WALL) {
            if (getPosY() >= GameConfig.SQUARE_HEIGHT) {
                return 0;
            } else {
                return 3;
            }
        }

        int collisionCase = 0;
        Square currentSquare = gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(x), calcMapIndexOfCoordinate(z));

        double squareCenterX = currentSquare.getIndexX() * GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE / 2;
        double squareCenterZ = currentSquare.getIndexZ() * GameConfig.SQUARE_SIZE + GameConfig.SQUARE_SIZE / 2;

        int horizontalRelativeToCenter = (x - squareCenterX <= 0) ? -1 : 1;
        int verticalRelativeToCenter = (z - squareCenterZ <= 0) ? -1 : 1;

        Square squareLeftRight = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX() + horizontalRelativeToCenter,
                currentSquare.getIndexZ());
        Square squareTopBottom = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX(),
                currentSquare.getIndexZ() + verticalRelativeToCenter);
        Square squareDiagonal = gameMap.getSquareAtIndexXZ(currentSquare.getIndexX() + horizontalRelativeToCenter,
                currentSquare.getIndexZ() + verticalRelativeToCenter);

        if (squareLeftRight.getType() == MapObjectType.WALL) {
            Vector3d origin = new Vector3d(
                    horizontalRelativeToCenter > 0 ? (currentSquare.getIndexX() + 1) * GameConfig.SQUARE_SIZE
                            : currentSquare.getIndexX() * GameConfig.SQUARE_SIZE,
                    0, 1);
            Vector3d line = new Vector3d(0, 1, 0);
            if (calcIntersectionWithLine(x, z, origin, line)) {
                if (getPosY() >= GameConfig.SQUARE_HEIGHT) {
                    collisionCase = 0;
                } else {
                    collisionCase += 1;
                }
            }
        }

        if (squareTopBottom.getType() == MapObjectType.WALL) {
            Vector3d origin = new Vector3d(0,
                    verticalRelativeToCenter > 0 ? (currentSquare.getIndexZ() + 1) * GameConfig.SQUARE_SIZE
                            : currentSquare.getIndexZ() * GameConfig.SQUARE_SIZE,
                    1);
            Vector3d line = new Vector3d(1, 0, 0);
            if (calcIntersectionWithLine(x, z, origin, line)) {
                if (getPosY() >= GameConfig.SQUARE_HEIGHT) {
                    collisionCase = 0;
                } else {
                    collisionCase += 2;
                }
            }
        }

        if (squareDiagonal.getType() == MapObjectType.WALL && collisionCase == 0) {
            double diagX = horizontalRelativeToCenter > 0 ? (currentSquare.getIndexX() + 1) * GameConfig.SQUARE_SIZE
                    : currentSquare.getIndexX() * GameConfig.SQUARE_SIZE;
            double diagZ = verticalRelativeToCenter > 0 ? (currentSquare.getIndexZ() + 1) * GameConfig.SQUARE_SIZE
                    : currentSquare.getIndexZ() * GameConfig.SQUARE_SIZE;
            double dist = Math.sqrt((diagX - x) * (diagX - x) + (diagZ - z) * (diagZ - z));
            if (dist <= this.radius)
                if (getPosY() >= GameConfig.SQUARE_HEIGHT) {
                    collisionCase = 0;
                } else {
                    collisionCase = 3;
                }
        }

        return collisionCase;
    }

    /**
     * calculates whether the player-cirlce intersects with a line
     *
     * @param xNew      target x-position
     * @param zNew      target z-position
     * @param origin
     * @param direction
     * @returns
     */
    public boolean calcIntersectionWithLine(double xNew, double zNew, Vector3d origin, Vector3d direction) {
        Vector3d line = origin.cross(direction);
        double dist = Math.abs(line.x * xNew + line.y * zNew + line.z) / Math.sqrt(line.x * line.x + line.y * line.y);
        return dist <= this.radius;
    }

    public void setQuaternion(double qX, double qY, double qZ, double qW) {
        quat.x = qX;
        quat.y = qY;
        quat.z = qZ;
        quat.w = qW;
    }

    public int calcMapIndexOfCoordinate(double a) {
        return (int) (a / GameConfig.SQUARE_SIZE);
    }

    public Vector3d getSpawn() {
        return spawn;
    }

    public void setSpawn(Vector3d spawn) {
        this.spawn = spawn;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public boolean squareUnderneathIsWall() {
        if (gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(position.x), calcMapIndexOfCoordinate(position.z)).getType() == MapObjectType.WALL) {
            return true;
        } else {
            return false;
        }
    }

    public void pushback() {
        double stepDistance = 0.1;
        Vector3d backward = new Vector3d(forward).normalize().negate();
        while (squareUnderneathIsWall()) {
            Vector3d displacement = new Vector3d(backward).mul(stepDistance);
            displacement.y = 0;
            position.add(displacement);
        }
        position.y = GameConfig.SNACKMAN_GROUND_LEVEL;
        //Verschiebe zusätzlich um den Radius entlang des Pushback-Vektors.
        Vector3d additionalDisplacement = new Vector3d(backward).mul(radius);
        additionalDisplacement.y = 0;
        position.add(additionalDisplacement);
    }

    public void push_forward() {
        double stepDistance = 0.1;
        Vector3d pushForwardVector = new Vector3d(0, 0, 1);
        while (squareUnderneathIsWall()) {
            Vector3d displacement = new Vector3d(pushForwardVector).mul(stepDistance);
            displacement.y = 0;
            position.add(displacement);
        }
        position.y = GameConfig.SNACKMAN_GROUND_LEVEL;
        Vector3d additionalDisplacement = new Vector3d(pushForwardVector).mul(radius);
        additionalDisplacement.y = 0;
        position.add(additionalDisplacement);
    }

    public void push_backward() {
        double stepDistance = 0.1;
        Vector3d pushBackwardVector = new Vector3d(0, 0, -1);
        while (squareUnderneathIsWall()) {
            Vector3d displacement = new Vector3d(pushBackwardVector).mul(stepDistance);
            displacement.y = 0;
            position.add(displacement);
        }
        position.y = GameConfig.SNACKMAN_GROUND_LEVEL;
        Vector3d additionalDisplacement = new Vector3d(pushBackwardVector).mul(radius);
        additionalDisplacement.y = 0;
        position.add(additionalDisplacement);
    }

    public void push_left() {
        double stepDistance = 0.1;
        Vector3d pushLeftVector = new Vector3d(-1, 0, 0);
        while (squareUnderneathIsWall()) {
            Vector3d displacement = new Vector3d(pushLeftVector).mul(stepDistance);
            displacement.y = 0;
            position.add(displacement);
        }
        position.y = GameConfig.SNACKMAN_GROUND_LEVEL;
        Vector3d additionalDisplacement = new Vector3d(pushLeftVector).mul(radius);
        additionalDisplacement.y = 0;
        position.add(additionalDisplacement);
    }

    public void push_right() {
        double stepDistance = 0.1;
        Vector3d pushRightVector = new Vector3d(1, 0, 0);
        while (squareUnderneathIsWall()) {
            Vector3d displacement = new Vector3d(pushRightVector).mul(stepDistance);
            displacement.y = 0;
            position.add(displacement);
        }
        position.y = GameConfig.SNACKMAN_GROUND_LEVEL;
        Vector3d additionalDisplacement = new Vector3d(pushRightVector).mul(radius);
        additionalDisplacement.y = 0;
        position.add(additionalDisplacement);
    }

    public int checkWallAlignment() {
        int mobX = calcMapIndexOfCoordinate(position.x);
        int mobZ = calcMapIndexOfCoordinate(position.z);
        // Prüfen, ob links, rechts, oben und/oder unten Wall-Elemente sind
        boolean leftWall = gameMap.getSquareAtIndexXZ(mobX - 1, mobZ).getType() == MapObjectType.WALL;
        boolean rightWall = gameMap.getSquareAtIndexXZ(mobX + 1, mobZ).getType() == MapObjectType.WALL;
        boolean topWall = gameMap.getSquareAtIndexXZ(mobX, mobZ - 1).getType() == MapObjectType.WALL;
        boolean bottomWall = gameMap.getSquareAtIndexXZ(mobX, mobZ + 1).getType() == MapObjectType.WALL;
        // Bedingung: links und rechts
        if (leftWall && rightWall && !topWall && !bottomWall) {
            return 1;
        }
        // Bedingung: oben und unten
        if (topWall && bottomWall && !leftWall && !rightWall) {
            return 2;
        }
        // Bedingung: links und unten
        if (!topWall && bottomWall && leftWall && !rightWall) {
            return 3;
        }
        // Bedingung: links und oben
        if (topWall && !bottomWall && leftWall && !rightWall) {
            return 4;
        }
        // Bedingung: oben und rechts
        if (topWall && !bottomWall && !leftWall && rightWall) {
            return 5;
        }
        // Bedingung: unten und rechts
        if (!topWall && bottomWall && !leftWall && rightWall) {
            return 6;
        }
        // Bedingung: links, unten und rechts
        if (!topWall && bottomWall && leftWall && rightWall) {
            return 7;
        }
        // Bedingung: links, oben und unten
        if (topWall && bottomWall && leftWall && !rightWall) {
            return 8;
        }
        // Bedingung: links, oben und rechts
        if (topWall && !bottomWall && leftWall && rightWall) {
            return 9;
        }
        // Bedingung: oben, unten und rechts
        if (topWall && bottomWall && !leftWall && rightWall) {
            return 10;
        }
        // Bedingung: unten
        if (!topWall && bottomWall && !leftWall && !rightWall) {
            return 11;
        }
        // Bedingung: links
        if (!topWall && !bottomWall && leftWall && !rightWall) {
            return 12;
        }
        // Bedingung: oben
        if (topWall && !bottomWall && !leftWall && !rightWall) {
            return 13;
        }
        // Bedingung: rechts
        if (!topWall && !bottomWall && !leftWall && rightWall) {
            return 14;
        }
        // Keine der Bedingungen erfüllt
        return 0;
    }


    public int getWallSection() {
        // Die Position des Mobs auf der Karte berechnen
        if (gameMap != null) {
            int mobX = calcMapIndexOfCoordinate(position.x);
            int mobZ = calcMapIndexOfCoordinate(position.z);
            Square square = gameMap.getSquareAtIndexXZ(mobX, mobZ);
            long idOfSquare = square.getId();
            double tempX = position.x;
            double tempZ = position.z;

            while (gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(tempX), calcMapIndexOfCoordinate(tempZ)).getId() == idOfSquare) {
                tempX = tempX - 1;
            }
            while (gameMap.getSquareAtIndexXZ(calcMapIndexOfCoordinate(tempX), calcMapIndexOfCoordinate(tempZ)).getId() == idOfSquare) {
                tempZ = tempZ - 1;
            }
            // Die Koordinaten des Wall-Elements
            double wallCenterX = tempX + (GameConfig.SQUARE_SIZE / 2);
            double wallCenterZ = tempZ + (GameConfig.SQUARE_SIZE / 2);
            //Berechnung der vier Bereiche:
            //Vergleiche die Position des Mobs mit dem Zentrum des Wall-Elements
            boolean isAboveCenter = position.z < wallCenterZ;
            boolean isLeftOfCenter = position.x < wallCenterX;
            //Bestimmen des Bereichs basierend auf der Position des Mobs
            if (isAboveCenter && isLeftOfCenter) {
                return 1; // Bereich 1 (oben links)
            } else if (isAboveCenter && !isLeftOfCenter) {
                return 2; // Bereich 2 (oben rechts)
            } else if (!isAboveCenter && isLeftOfCenter) {
                return 3; // Bereich 3 (unten links)
            } else if (!isAboveCenter && !isLeftOfCenter) {
                return 4; // Bereich 4 (unten rechts)
            }
        }

        return 0; //Keine gültige Bereichszuordnung
    }

    @Override
    public String toString() {
        return "Mob{" +
                "id=" + id +
                ", position=" + position +
                ", radius=" + radius +
                ", quat=" + quat +
                ", speed=" + speed +
                ", spawn=" + spawn +
                '}';
    }
}
