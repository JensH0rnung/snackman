package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import java.util.List;

import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters.Behavior;

public class StrategyChicken {

    public Behavior behavior;

    public StrategyChicken(Behavior behavior){
        this.behavior = behavior;
    }

    public List<String>  act(List<String> squares){
        List<String> result = behavior.execute(squares);
        return result;
    }



}
