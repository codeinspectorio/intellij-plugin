package io.codiga.plugins.jetbrains.actions.snippet_search;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Mouse listener to redirect the user to the login page.
 */
public class OwnerMouseListener implements MouseListener {
    private String accountType;
    private String username;

    public OwnerMouseListener(String _accountType, String _username) {
        this.accountType = _accountType;
        this.username = _username;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        String userLink = String.format("https://app.codiga.io/hub/user/%s/%s", accountType.toLowerCase(), username);
        try {
            Desktop.getDesktop().browse(new URI(userLink));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
