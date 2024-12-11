package de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters.TalaChicken;

import de.hsrm.mi.swt.snackman.entities.mob.Chicken.Chicken;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

import de.hsrm.mi.swt.snackman.entities.mob.EatingMob;

public class TalaChicken extends Chicken {

    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();
    private final Logger logger = LoggerFactory.getLogger(TalaChicken.class);

    public void initJython() {
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mob/Chicken/Characters/TalaChicken");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        pythonInterpreter = new PythonInterpreter();
        logger.info("Initialised jython for chicken movement");
    }

    public List<String> executeMovementSkript(List<String> squares) {
        try {
            logger.info("Running python chicken script with: {}", squares.toString());
            pythonInterpreter.exec("from TalaChickenMovementSkript import choose_next_square");
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

    public List<String> chickenFollowsSnackmanWithBigDistance(List<String> squares){
        try {
            logger.info("Running python chicken script with: {}", squares.toString());
            pythonInterpreter.exec("from TalaChickenMovementSkript import choose_next_square_to_get_to_SnackMan");
            PyObject func = pythonInterpreter.get("choose_next_square_to_get_to_SnackMan");
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
}
