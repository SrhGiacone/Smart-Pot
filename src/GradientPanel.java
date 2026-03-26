import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ---------------------------
        // 1) Hintergrund
        // ---------------------------
        Color oben = new Color(245, 250, 240);
        Color unten = new Color(140, 185, 145);

        GradientPaint verlauf = new GradientPaint(
                0, 0, oben,
                getWidth(), getHeight(), unten
        );

        g2d.setPaint(verlauf);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // ---------------------------
        // 2) Rahmen-Einstellungen
        // ---------------------------
        int randDicke = 18;
        int eckQuadratGroesse = 34;

        // Holz-Grundfarbe
        Color holzGrund = new Color(135, 85, 45);
        Color holzHell = new Color(175, 120, 75, 110);
        Color holzDunkel = new Color(75, 45, 20, 120);
        Color eckenFarbe = new Color(95, 60, 30);

        // ---------------------------
        // 3) Holzrahmen zeichnen
        // ---------------------------
        // oben
        g2d.setColor(holzGrund);
        g2d.fillRect(0, 0, getWidth(), randDicke);
        maleHorizontaleMaserung(g2d, 0, 0, getWidth(), randDicke, holzHell, holzDunkel);

        // unten
        g2d.setColor(holzGrund);
        g2d.fillRect(0, getHeight() - randDicke, getWidth(), randDicke);
        maleHorizontaleMaserung(g2d, 0, getHeight() - randDicke, getWidth(), randDicke, holzHell, holzDunkel);

        // links
        g2d.setColor(holzGrund);
        g2d.fillRect(0, 0, randDicke, getHeight());
        maleVertikaleMaserung(g2d, 0, 0, randDicke, getHeight(), holzHell, holzDunkel);

        // rechts
        g2d.setColor(holzGrund);
        g2d.fillRect(getWidth() - randDicke, 0, randDicke, getHeight());
        maleVertikaleMaserung(g2d, getWidth() - randDicke, 0, randDicke, getHeight(), holzHell, holzDunkel);

        // ---------------------------
        // 4) Eck-Quadrate
        // ---------------------------
        g2d.setColor(eckenFarbe);

        // oben links
        g2d.fillRect(0, 0, eckQuadratGroesse, eckQuadratGroesse);

        // oben rechts
        g2d.fillRect(getWidth() - eckQuadratGroesse, 0, eckQuadratGroesse, eckQuadratGroesse);

        // unten links
        g2d.fillRect(0, getHeight() - eckQuadratGroesse, eckQuadratGroesse, eckQuadratGroesse);

        // unten rechts
        g2d.fillRect(getWidth() - eckQuadratGroesse, getHeight() - eckQuadratGroesse,
                eckQuadratGroesse, eckQuadratGroesse);

        // Eck-Quadrate auch leicht "holzig" machen
        maleHorizontaleMaserung(g2d, 0, 0, eckQuadratGroesse, eckQuadratGroesse, holzHell, holzDunkel);
        maleHorizontaleMaserung(g2d, getWidth() - eckQuadratGroesse, 0, eckQuadratGroesse, eckQuadratGroesse, holzHell, holzDunkel);
        maleHorizontaleMaserung(g2d, 0, getHeight() - eckQuadratGroesse, eckQuadratGroesse, eckQuadratGroesse, holzHell, holzDunkel);
        maleHorizontaleMaserung(g2d, getWidth() - eckQuadratGroesse, getHeight() - eckQuadratGroesse,
                eckQuadratGroesse, eckQuadratGroesse, holzHell, holzDunkel);

        g2d.dispose();
    }

    // Maserung für obere/untere Holzleisten
    private void maleHorizontaleMaserung(Graphics2D g2d, int x, int y, int breite, int hoehe,
                                         Color hell, Color dunkel) {

        for (int i = 0; i < hoehe; i += 2) {
            int verschiebung = (int) (Math.sin((x + i) * 0.12) * 3);

            g2d.setColor(i % 4 == 0 ? hell : dunkel);
            g2d.drawLine(x, y + i, x + breite + verschiebung, y + i);
        }

        // ein paar dunklere "Jahresringe"
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setColor(new Color(65, 40, 20, 90));

        for (int i = 8; i < hoehe; i += 6) {
            int wellung = (int) (Math.sin(i * 0.7) * 4);
            g2d.drawLine(x, y + i, x + breite + wellung, y + i);
        }
    }

    // Maserung für linke/rechte Holzleisten
    private void maleVertikaleMaserung(Graphics2D g2d, int x, int y, int breite, int hoehe,
                                       Color hell, Color dunkel) {

        for (int i = 0; i < breite; i += 2) {
            int verschiebung = (int) (Math.sin((y + i) * 0.15) * 3);

            g2d.setColor(i % 4 == 0 ? hell : dunkel);
            g2d.drawLine(x + i, y, x + i, y + hoehe + verschiebung);
        }

        // ein paar dunklere "Jahresringe"
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.setColor(new Color(65, 40, 20, 80));

        for (int i = 5; i < breite; i += 5) {
            int wellung = (int) (Math.sin(i * 0.9) * 3);
            g2d.drawLine(x + i, y, x + i + wellung, y + hoehe);
        }
    }
}