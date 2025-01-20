package de.hsrm.mi.swt.snackman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.controller.GameMap.GameMapController;

@SpringBootTest
public class MapUploadTest {
    
    @Autowired
    private GameMapController gameMapController;

    private static final Path workFolder = Paths.get("./extensions").toAbsolutePath();

    @BeforeAll
    static void fileSetUp() {
        try{
            tearDownAfter();  
        }catch(Exception e){
            System.out.println("No file to delete");
        }   
        SnackmanApplication.checkAndCopyResources();
        assert Files.exists(workFolder.resolve("maze"));
        assert Files.exists(workFolder.resolve("map"));
    }

    @AfterAll
    static void tearDownAfter() throws IOException {
        if (Files.exists(workFolder)) {
            FileSystemUtils.deleteRecursively(workFolder.toFile());
        }
    }

    @Test
    void testUploadMap_InvalidMapContent(){
        String invalidMap = "A S G \nC o # #";
        MockMultipartFile file = new MockMultipartFile(
            "file", "invalidMap.txt","./extension/map", invalidMap.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<String> response = gameMapController.uploadMap(file, "1");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The map file is only allowed to contain the following characters: S, G, C, o, #, and spaces. Please do not use the Tab key instead of spaces when creating the .txt file.", 
                    response.getBody());
    }

    @ParameterizedTest
    @CsvSource({
        "'S G G G\nC o G #', 'nonSquareMap.txt', 'The map must be a square, meaning the number of characters in each row must equal the total number of rows.'",
        "'G G \nCo #\nG G \nCo #', 'noSnackman.txt', 'The map file must contain exactly one ''S''.'",
        "'S S \nCo #\nG G \nCo #', 'multipleSnackman.txt', 'The map file must contain exactly one ''S''.'",
        "'S G \nCo #\nG C \nCo #', 'notEnoughGhosts.txt', 'The map file must contain at least 4 ''G''s for 4 ghost player.'"
    })
    void testUploadMap_InvalidCases(String mapContent, String fileName, String expectedMessage) {
        MockMultipartFile file = new MockMultipartFile(
            "file", fileName, "./extension/map", mapContent.getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<String> response = gameMapController.uploadMap(file, "1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

}
