
package org.damcode.game.c4;

import java.awt.Dialog;
import javax.swing.JDialog;

/**
 *
 * @author dm
 */
public class C4MessageDialog extends JDialog {

    public C4MessageDialog(Dialog owner, String title, String message) {
        super(owner, title, false);
    }
}
