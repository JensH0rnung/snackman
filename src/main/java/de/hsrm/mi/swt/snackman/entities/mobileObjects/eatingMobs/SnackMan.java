package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs;

import de.hsrm.mi.swt.snackman.controller.PlayerMovement.SnackManUpdateDTO;
import de.hsrm.mi.swt.snackman.entities.map.Square;
import de.hsrm.mi.swt.snackman.messaging.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import de.hsrm.mi.swt.snackman.configuration.GameConfig;
import de.hsrm.mi.swt.snackman.services.MapService;

import java.beans.PropertyChangeEvent;


@Component
public class SnackMan extends EatingMob {

    private FrontendMessageService frontendMessageService;

    @Autowired
    public SnackMan(MapService mapService){
        this(mapService, GameConfig.SNACKMAN_SPEED, GameConfig.SNACKMAN_RADIUS);
    }

    public SnackMan(MapService mapService, int speed, double radius){
        super(mapService, speed, radius );    }

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

}
