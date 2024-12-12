package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters.Behavior;
import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters.TalaChickenBehavior;

public class DefaultChickenBehaviour implements Behavior{

    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();
    private final Logger logger = LoggerFactory.getLogger(TalaChickenBehavior.class);

    @Override
    public List<String>  execute(List<String> squares) {
        executeMovementSkript(squares);
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    public List<String> chooseWalkingPath(List<String> currentlyVisibleEnvironment) {
        return executeMovementSkript(currentlyVisibleEnvironment);
    }

    public void initJython() {
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mob/Chicken");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        this.pythonInterpreter = new PythonInterpreter();
        logger.info("Initialised jython for chicken movement");
    }

    public List<String> executeMovementSkript(List<String> squares) {
        try {
            logger.info("Running python chicken script with: {}", squares.toString());
            pythonInterpreter.exec("from ChickenMovementSkript import choose_next_square");
            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(squares));

            if (result instanceof PyList) {
                PyList pyList = (PyList) result;
                return convertPythonList(pyList);
            }

            throw new Exception("Python chicken script did not load.");
        } catch (Exception ex) {
            logger.error("Error while executing chicken python script: ", ex);
            ex.printStackTrace();
        }
        return squares;
    }

    protected List<String> convertPythonList(PyList pyList) {
        List<String> javaList = new ArrayList<>();
        for (Object item : pyList) {
            javaList.add(item.toString());
        }
        logger.info("Python script result is {}", javaList.toString());
        return javaList;
    }
    
}
