package de.hsrm.mi.swt.snackman.controller.SnackMan;

import de.hsrm.mi.swt.snackman.controller.PlayerMovement.SnackManUpdateDTO;
import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.SnackMan;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnackManController {


    @Autowired
    private SnackMan snackman;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @PostConstruct
    public void initListener(){
        snackman.addPropertyChangeListener(event -> {
            if ( "currentCalories".equals(event.getPropertyName()) ){
                int newCalories = (int) event.getNewValue();
                String message = newCalories == snackman.getMAXCALORIES() ? "Maximum calories reached!" : "";
                SnackManUpdateDTO update = new SnackManUpdateDTO(newCalories, message);

                messagingTemplate.convertAndSend("/topic/update", update);
            }

        });
    }

}
