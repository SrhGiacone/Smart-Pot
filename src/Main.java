import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {

    // ntfy-Topic für dein Handy
    private static final String NTFY_TOPIC = "bonsai-manuel-8266";

    // Merkt sich die letzte Trockenheits-Stufe, für die schon eine Push gesendet wurde
    // Startwert 31 sorgt dafür, dass bei 30 % die erste Warnung kommt
    private static int letzteWarnstufe = 31;

    // Verhindert, dass die "Dankeschön"-Nachricht bei > 70 % dauernd neu gesendet wird
    private static boolean dankeschoenSchonGesendet = false;

    // Referenz auf die GUI, damit der Feuchtigkeitswert im Fenster angezeigt werden kann
    private static GUI gui;

    public static void main(String[] args) throws IOException {

        // GUI starten
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bonsai Monitor");
            gui = new GUI();

            // GUI in ScrollPane packen, damit bei kleineren Fenstern nichts abgeschnitten wird
            JScrollPane scrollPane = new JScrollPane(gui.getPanel1());
            scrollPane.setBorder(null);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

            // Scrollleisten nur dann zeigen, wenn wirklich nötig
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // Scrollen angenehmer machen
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            // Hintergrund mit Holzrahmen bleibt außen
            GradientPanel hintergrundPanel = new GradientPanel();
            hintergrundPanel.setLayout(new BorderLayout());
            hintergrundPanel.add(scrollPane, BorderLayout.CENTER);

            frame.setContentPane(hintergrundPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Mindestgröße, damit die GUI nicht komplett zerquetscht wird
            frame.setMinimumSize(new Dimension(1100, 750));

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // HTTP-Server starten
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/data", Main::handleData);
        server.setExecutor(null);
        server.start();

        System.out.println("Server läuft auf Port 8080");
    }

    // Wird aufgerufen, wenn Daten an /data gesendet werden
    private static void handleData(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);

        String wert = params.getOrDefault("wert", "kein wert");
        System.out.println("Empfangen vom ESP: " + wert);

        pruefeFeuchtigkeitUndSendeWarnung(wert);

        String response = "OK empfangen: " + wert;
        exchange.sendResponseHeaders(200, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    // Prüft den Feuchtigkeitswert, aktualisiert die GUI und sendet ggf. Push-Nachrichten
    private static void pruefeFeuchtigkeitUndSendeWarnung(String wert) {
        Integer prozent = extrahiereProzent(wert);
        if (prozent == null) {
            System.out.println("Konnte Feuchtigkeitswert nicht lesen: " + wert);
            return;
        }

        // Wert in der GUI anzeigen
        if (gui != null) {
            gui.zeigeEmpfangenenWert(prozent + " %");
        }

        // Wenn der Wert über 70 % ist:
        // einmalige "Dankeschön"-Push senden
        if (prozent > 70) {
            if (!dankeschoenSchonGesendet) {
                sendeNtfyPush(
                        "Bonsai",
                        "Dankeschön, jetzt geht's mir schon besser."
                );
                dankeschoenSchonGesendet = true;
            }

            // Trockenheitswarnungen zurücksetzen
            letzteWarnstufe = 31;
            return;
        }

        // Sobald der Wert wieder 70 % oder weniger ist,
        // darf später erneut eine Dankeschön-Nachricht gesendet werden
        dankeschoenSchonGesendet = false;

        // Wenn der Wert wieder über 30 % ist,
        // sollen die Warnstufen für später wieder neu starten können
        if (prozent > 30) {
            letzteWarnstufe = 31;
            return;
        }

        int aktuelleWarnstufe;

        // Warnstufen:
        // 30, 25, 20, 15, 10, 5, 0
        if (prozent > 25) {
            aktuelleWarnstufe = 30;
        } else if (prozent > 20) {
            aktuelleWarnstufe = 25;
        } else if (prozent > 15) {
            aktuelleWarnstufe = 20;
        } else if (prozent > 10) {
            aktuelleWarnstufe = 15;
        } else if (prozent > 5) {
            aktuelleWarnstufe = 10;
        } else if (prozent > 0) {
            aktuelleWarnstufe = 5;
        } else {
            aktuelleWarnstufe = 0;
        }

        // Nur dann Push senden, wenn eine neue niedrigere Warnstufe erreicht wurde
        if (aktuelleWarnstufe < letzteWarnstufe) {
            sendeNtfyPush(
                    "Bonsai braucht Wasser",
                    holeWarnungsText(aktuelleWarnstufe)
            );
            letzteWarnstufe = aktuelleWarnstufe;
        }
    }

    // Gibt für jede Warnstufe den passenden Push-Text zurück
    private static String holeWarnungsText(int warnstufe) {
        switch (warnstufe) {
            case 30:
                return "Hey, ich habe Durst.";
            case 25:
                return "Erinnerung: Ich bin immer noch durstig.";
            case 20:
                return "Hey, gieß mich doch mal.";
            case 15:
                return "Erinnerung: Mir fehlt noch Wasser.";
            case 10:
                return "Hast du mich vergessen?";
            case 5:
                return "Erinnerung: Bitte gieß mich jetzt.";
            case 0:
                return "Du hast mich einfach nicht mehr lieb :(";
            default:
                return "Bonsai braucht Wasser.";
        }
    }

    // Holt nur die Zahl aus dem empfangenen String
    // Beispiel:
    // "25" -> 25
    // "25 %" -> 25
    private static Integer extrahiereProzent(String wert) {
        if (wert == null) {
            return null;
        }

        String nurZahlen = wert.replaceAll("[^0-9]", "");
        if (nurZahlen.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(nurZahlen);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Sendet eine Push-Benachrichtigung an ntfy
    private static void sendeNtfyPush(String titel, String nachricht) {
        HttpURLConnection verbindung = null;

        try {
            URL url = new URL("https://ntfy.sh/" + NTFY_TOPIC);
            verbindung = (HttpURLConnection) url.openConnection();
            verbindung.setRequestMethod("POST");
            verbindung.setDoOutput(true);

            verbindung.setRequestProperty("Title", titel);
            verbindung.setRequestProperty("Priority", "5");
            verbindung.setRequestProperty("Tags", "warning,seedling");
            verbindung.setRequestProperty("Content-Type", "text/plain; charset=utf-8");

            byte[] daten = nachricht.getBytes(StandardCharsets.UTF_8);
            verbindung.setFixedLengthStreamingMode(daten.length);

            try (OutputStream os = verbindung.getOutputStream()) {
                os.write(daten);
            }

            int code = verbindung.getResponseCode();
            System.out.println("ntfy Status: " + code);
        } catch (Exception e) {
            System.out.println("Fehler beim Senden der Handy-Benachrichtigung:");
            e.printStackTrace();
        } finally {
            if (verbindung != null) {
                verbindung.disconnect();
            }
        }
    }

    // Zerlegt die URL-Query in key=value-Paare
    // Beispiel: wert=25
    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return map;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }

        return map;
    }
}