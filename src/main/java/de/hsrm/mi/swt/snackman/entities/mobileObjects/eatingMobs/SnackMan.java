package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.entities.mapObject.snack.Snack;
import de.hsrm.mi.swt.snackman.services.MapService;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

@Component
public class SnackMan extends EatingMob {

   /* @Autowired
    private ApplicationEventPublisher eventPublisher;
    */

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private int currentCalories;
    private final int MAXCALORIES = 3000;

    private double posX;
    private double posY;
    private double posZ;
    private double radius;
    private Quaterniond quat;
    private Square currentSquare;

    private MapService mapService;




    @Autowired
    public SnackMan(MapService mapService){
        this(mapService, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);
    }

    public SnackMan(MapService mapService, int speed, double radius){
        super(mapService, speed, radius);    }

    public SnackMan(MapService mapService, int speed, double radius, double posX, double posY, double posZ){
        super(mapService, speed, radius, posX, posY, posZ);
    }

    @Override
    public void gainKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gainKcal'");
    }

    @Override
    public void loseKcal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loseKcal'");
    }

    public void jump(){

    }

    private void jumpOverChicken(){

    }

    private void jumpToSeeMap(){

    }

    private void jumpOverWall(){

    }

    public void collectItems(){

    }

    @Override
    public void move(double x, double y, double z) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }

    public int getCurrentCalories() {
        return currentCalories;
    }

    public int getMAXCALORIES(){
        return MAXCALORIES;
    }

    /**
     * Collects the snack on the square if there is one.
     * If there is one that remove it from the square.
     * @param square to eat the snack from
     */
    public void consumeSnackOnSquare(Square square){
        Snack snackOnSquare = square.getSnack();

        if(snackOnSquare != null){
            int oldCalories = this.currentCalories;

            if ( (currentCalories + snackOnSquare.getCalories()) >= MAXCALORIES ){
                currentCalories = MAXCALORIES;
            }
            else{
            currentCalories += snackOnSquare.getCalories();
            }
            square.setSnack(null);

            propertyChangeSupport.firePropertyChange("currentCalories", oldCalories, currentCalories);
        }
    }


    // Listener hinzufügen
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    // Listener entfernen
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
