package utility;

import javafx.scene.control.TableCell;

import java.time.ZonedDateTime;

/** This class is necessary for the proper formatting of start/end dateTimes on the appointments tableviews on the main menu and the reports menu.
 * @author James Wilson
 */
public class DateTimeFormatCell extends TableCell<Object, ZonedDateTime> {

    public DateTimeFormatCell(){};

    /**
     * @param item The utcStart or utcEnd ZonedDateTime of an appointment
     * @param isEmpty Boolean regarding the data for the cell.
     */
    @Override
    protected void updateItem(ZonedDateTime item, boolean isEmpty) {
        super.updateItem(item, isEmpty);

        if (isEmpty == true || item == null) {
            setText(null);
            setGraphic(null);
        }
        else {
            ZonedDateTime localZDT = Time.utcToLocal(item);
            String localDate = localZDT.toLocalDate().toString();
            String localTime = localZDT.toLocalTime().toString();
            setText((localDate + "/" + localTime));
        }
    }
}
