import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class WoodButtonUI extends BasicButtonUI {

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        AbstractButton button = (AbstractButton) c;
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(new Color(40, 20, 5));
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(12, 28, 12, 28));
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = c.getWidth();
        int h = c.getHeight();
        int arc = 22;

        // Holzfarben je nach Zustand
        Color oben;
        Color unten;
        Color rand;
        Color textFarbe;

        if (model.isPressed()) {
            oben = new Color(120, 74, 38);
            unten = new Color(90, 52, 25);
            rand = new Color(70, 40, 18);
            textFarbe = new Color(255, 245, 220);
        } else if (model.isRollover()) {
            oben = new Color(185, 125, 70);
            unten = new Color(145, 92, 48);
            rand = new Color(95, 58, 28);
            textFarbe = new Color(35, 18, 5);
        } else {
            oben = new Color(170, 110, 60);
            unten = new Color(130, 78, 40);
            rand = new Color(90, 55, 28);
            textFarbe = new Color(30, 15, 5);
        }

        // Grundkörper mit Verlauf
        GradientPaint gp = new GradientPaint(0, 0, oben, 0, h, unten);
        g2d.setPaint(gp);
        g2d.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // Holzmaserung
        for (int y = 6; y < h - 6; y += 4) {
            int wellung = (int) (Math.sin(y * 0.55) * 4);

            g2d.setColor(new Color(255, 220, 170, 35));
            g2d.drawLine(12, y, w - 12 + wellung, y);

            g2d.setColor(new Color(70, 35, 10, 30));
            g2d.drawLine(12, y + 1, w - 12 - wellung, y + 1);
        }

        // Leichte Glanzkante oben
        g2d.setColor(new Color(255, 240, 210, 60));
        g2d.drawRoundRect(2, 2, w - 5, h / 2, arc, arc);

        // Außenrand
        g2d.setColor(rand);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

        // Textfarbe setzen
        button.setForeground(textFarbe);

        g2d.dispose();

        super.paint(g, c);
    }
}