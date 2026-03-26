import javax.swing.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.awt.Image;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class GUI {
    private JPanel panel1;
    private JTextField feuchtigkeitFeld;
    private JButton gegossenButton;
    private JLabel uhrzeitLabel;
    private JLabel zuletztGegossenLabel;
    private JLabel zuletztGedüngtLabel;
    private JLabel zuletztGegossenWertLabel;
    private JLabel zuletztGedüngtWertLabel;
    private JButton geduengtButton;
    private JLabel bonsaiLabel;
    private JPanel kopfPanel;

    public GUI() {
        panel1.setOpaque(false);
        starteUhr();
        setzeAktionen();
        ladeBonsaiBild();
        stylePanels();
        styleButtons();
    }

    private void styleButtons() {
        gegossenButton.setUI(new WoodButtonUI());
        geduengtButton.setUI(new WoodButtonUI());
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public void zeigeEmpfangenenWert(String wert) {
        SwingUtilities.invokeLater(() -> feuchtigkeitFeld.setText(wert));
    }

    private void stylePanels() {
        panel1.setBorder(new javax.swing.border.EmptyBorder(80, 90, 80, 90));


        feuchtigkeitFeld.setHorizontalAlignment(JTextField.CENTER);
    }

    private void starteUhr() {
        Timer timer = new Timer(1000, e -> {
            String zeit = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            uhrzeitLabel.setText(zeit);
        });

        timer.setInitialDelay(0);
        timer.start();
    }

    private void setzeAktionen() {
        gegossenButton.addActionListener(e -> {
            String zeitstempel = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            zuletztGegossenWertLabel.setText(zeitstempel);
        });

        geduengtButton.addActionListener(e -> {
            String zeitstempel = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            zuletztGedüngtWertLabel.setText(zeitstempel);
        });
    }
    private void ladeBonsaiBild() {
        ImageIcon icon = new ImageIcon("bonsai.png");

        Image skaliertesBild = icon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);

        bonsaiLabel.setText("");
        bonsaiLabel.setIcon(new ImageIcon(skaliertesBild));
        bonsaiLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }




    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}