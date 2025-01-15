package de.hsrm.mi.swt.snackman.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;

@Service
public class ReadMazeService {

    private final String PYTHON_FILE_PATH = "./extensions/maze"; 
    private PythonInterpreter interpreter;

    public ReadMazeService(){
        System.setProperty("python.import.site", "false");
        interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("sys.path.append('" + PYTHON_FILE_PATH + "')");
    }

    /**
     * Reads maze data from a file and converts it into a char array with [x][z]-coordinates
     *
     * @param filePath the path to the file containing the maze data
     * @return a char array representing the maze
     * @throws RuntimeException if there's an error reading the file
     */
    public char[][] readMazeFromFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the maze file.", e);
        }

        if (lines.isEmpty()) {
            throw new RuntimeException("Maze file is empty.");
        }

        int rows = lines.size();
        int cols = lines.getFirst().length();
        char[][] mazeAsCharArray = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            mazeAsCharArray[i] = lines.get(i).toCharArray();
        }

        return mazeAsCharArray;
    }

    public void generateNewMaze() {
        interpreter.exec("from Maze import *");
        interpreter.exec("main()");
    }
}
