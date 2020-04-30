/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package librarymanagementsystem.model;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author Bright
 */
public class Notification {

    public Notification(String title, String text, int seonds) {
        Notifications notifications = Notifications.create()
                .title(title)
                .text(text)
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(seonds));
        switch (title) {
            case "Information":
                notifications.showInformation();
                break;
            case "Message":
                notifications.showInformation();
                break;
            case "Error":
                notifications.showError();
                break;
            case "Warning":
                notifications.showWarning();
                break;
            default:
                break;
        }
    }
}
