package utility;

import javafx.event.ActionEvent;
import java.io.IOException;

/** When implemented, this interface will be utilized in order to load a screen.
 * It is appropriate to use this interface for that purpose. There are numerous points throughout the program in which the loading of pages must be coded.
 * Without the use of a lambda expression/functional interface, each of these points would create several lines of duplicate code.
 * @author James Wilson
 */
public interface SceneInterface {
    /**
     * @param event A button that leads to the loading of a page is clicked.
     * @param location The address of the file containing the page to be loaded.
     * @throws IOException Error loading file;
     */
    void loadScene(ActionEvent event, String location) throws IOException;
}
