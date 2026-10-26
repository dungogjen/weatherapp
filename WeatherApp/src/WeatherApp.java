import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class WeatherApp extends JFrame {

    private final JTextField cityField;
    private final JButton searchButton;
    private final JLabel resultLabel;
    private final WeatherService weatherService;

    private final Color darkBlue = new Color(10, 18, 48);
    private final Color cardBlue = new Color(22, 34, 72);
    private final Color orange = new Color(255, 145, 40);
    private final Color white = new Color(245, 247, 255);

    public WeatherApp() {
        weatherService = new WeatherService();

        setTitle("Halloween Weather App");
        setSize(500, 610);
        setMinimumSize(new Dimension(460, 570));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Halloween night background
        BackgroundPanel background = new BackgroundPanel();
        background.setLayout(new BorderLayout(15, 15));
        background.setBorder(
            new EmptyBorder(25, 30, 25, 30)
        );

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel moonLabel = new JLabel("\u263E");
        moonLabel.setFont(new Font("Serif", Font.PLAIN, 36));
        moonLabel.setForeground(new Color(255, 220, 130));
        moonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("WEATHER APP");
        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 29)
        );
        titleLabel.setForeground(orange);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
            "TRICK OR TREAT • CHECK THE WEATHER"
        );
        subtitleLabel.setFont(
            new Font("Arial", Font.PLAIN, 11)
        );
        subtitleLabel.setForeground(
            new Color(190, 200, 230)
        );
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(moonLabel);
        header.add(Box.createVerticalStrut(3));
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitleLabel);

        // Main weather card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardBlue);
        card.setBorder(
            new CompoundBorder(
                new LineBorder(orange, 2, true),
                new EmptyBorder(22, 22, 22, 22)
            )
        );

        JLabel cityLabel = new JLabel("CITY NAME");
        cityLabel.setFont(
            new Font("Arial", Font.BOLD, 13)
        );
        cityLabel.setForeground(white);
        cityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cityField = new JTextField();
        cityField.setFont(
            new Font("Arial", Font.PLAIN, 16)
        );
        cityField.setForeground(white);
        cityField.setCaretColor(orange);
        cityField.setBackground(
            new Color(12, 23, 53)
        );
        cityField.setBorder(
            new CompoundBorder(
                new LineBorder(
                    new Color(92, 112, 160), 1, true
                ),
                new EmptyBorder(10, 12, 10, 12)
            )
        );
        cityField.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 44)
        );
        cityField.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchButton = new JButton("SEARCH WEATHER");
        searchButton.setFont(
            new Font("Arial", Font.BOLD, 14)
        );
        searchButton.setForeground(darkBlue);
        searchButton.setBackground(orange);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );
        searchButton.setBorder(
            new EmptyBorder(12, 10, 12, 10)
        );
        searchButton.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 46)
        );
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchButton.setOpaque(true);

        resultLabel = new JLabel(
            "<html><div style='text-align:center;'>"
            + "<font color='#FFA040' size='5'>"
            + "&#127875;</font><br><br>"
            + "<font color='#F5F7FF'>Your forecast awaits...</font>"
            + "<br><br><font color='#BAC6E8' size='3'>"
            + "Enter a city to reveal its weather."
            + "</font></div></html>",
            SwingConstants.CENTER
        );
        resultLabel.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setPreferredSize(new Dimension(350, 155));
        resultLabel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 190)
        );

        card.add(cityLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(cityField);
        card.add(Box.createVerticalStrut(15));
        card.add(searchButton);
        card.add(Box.createVerticalStrut(20));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(15));
        card.add(resultLabel);

        // Center the card
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(card, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel(
            "☾  SPOOKY SKIES • HALLOWEEN EDITION  ☽",
            SwingConstants.CENTER
        );
        footer.setFont(
            new Font("Arial", Font.BOLD, 10)
        );
        footer.setForeground(
            new Color(190, 200, 230)
        );

        background.add(header, BorderLayout.NORTH);
        background.add(centerPanel, BorderLayout.CENTER);
        background.add(footer, BorderLayout.SOUTH);

        setContentPane(background);

        searchButton.addActionListener(e -> searchWeather());
        cityField.addActionListener(e -> searchWeather());
    }

    private void searchWeather() {
        String city = cityField.getText().trim();

        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a city name.",
                "Missing City",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        searchButton.setEnabled(false);
        searchButton.setText("SUMMONING FORECAST...");
        resultLabel.setText(
            "<html><center><font color='#FFA040'>"
            + "&#127875; Gathering weather data..."
            + "</font></center></html>"
        );

        SwingWorker<WeatherData, Void> worker =
            new SwingWorker<WeatherData, Void>() {

            @Override
            protected WeatherData doInBackground()
                    throws Exception {
                return weatherService.getWeather(city);
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                searchButton.setText("SEARCH WEATHER");

                try {
                    WeatherData data = get();

                    resultLabel.setText(String.format(
                        "<html><div style='text-align:center;'>"
                        + "<font color='#FFA040' size='5'>"
                        + "&#127875; WEATHER REPORT</font><br><br>"
                        + "<font color='#FFFFFF' size='5'><b>%s</b></font>"
                        + "<br><br>"
                        + "<font color='#FFD166' size='6'><b>%.1f °C</b></font>"
                        + "<br><br>"
                        + "<font color='#E0E7FF'>"
                        + "Condition: %s<br><br>"
                        + "Humidity: %d%%<br><br>"
                        + "Wind Speed: %.1f m/s"
                        + "</font></div></html>",
                        escapeHtml(data.getCity()),
                        data.getTemperature(),
                        escapeHtml(data.getCondition()),
                        data.getHumidity(),
                        data.getWindSpeed()
                    ));

                } catch (Exception ex) {
                    resultLabel.setText(
                        "<html><center>"
                        + "<font color='#FF7777'>"
                        + "&#127875; Weather unavailable!"
                        + "</font><br><br>"
                        + "<font color='#FFFFFF'>"
                        + "Check your city, internet, and API key."
                        + "</font></center></html>"
                    );

                    JOptionPane.showMessageDialog(
                        WeatherApp.this,
                        "Could not retrieve weather data.\n"
                        + "Check your city name, internet connection, "
                        + "and API key.",
                        "Weather Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }

    // Draw the night sky, moon, stars, and bats using Java code
    private class BackgroundPanel extends JPanel {

        private final int[][] stars = new int[65][3];

        BackgroundPanel() {
            Random random = new Random(17);

            for (int i = 0; i < stars.length; i++) {
                stars[i][0] = random.nextInt(700);
                stars[i][1] = random.nextInt(700);
                stars[i][2] = 1 + random.nextInt(3);
            }

            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Midnight blue gradient
            g2.setPaint(new GradientPaint(
                0, 0, new Color(7, 13, 38),
                getWidth(), getHeight(),
                new Color(34, 24, 76)
            ));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Stars
            g2.setColor(new Color(255, 230, 170));

            for (int[] star : stars) {
                int x = Math.floorMod(star[0], getWidth());
                int y = Math.floorMod(star[1], getHeight());

                g2.fillOval(x, y, star[2], star[2]);
            }

            // Crescent moon
            int moonX = getWidth() - 78;
            int moonY = 32;

            g2.setColor(new Color(255, 222, 145));
            g2.fillOval(moonX, moonY, 38, 38);

            g2.setColor(new Color(19, 19, 57));
            g2.fillOval(moonX + 12, moonY - 7, 38, 38);

            // Decorative bats
            drawBat(g2, 30, 95, 0.8);
            drawBat(g2, getWidth() - 65, 125, 0.65);
            drawBat(g2, getWidth() - 45, getHeight() - 80, 0.55);

            // Orange horizon
            g2.setColor(new Color(255, 145, 40, 90));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(
                0, getHeight() - 12,
                getWidth(), getHeight() - 12
            );

            g2.dispose();
        }

        private void drawBat(
                Graphics2D g2, int x, int y, double scale) {

            Graphics2D bat = (Graphics2D) g2.create();
            bat.translate(x, y);
            bat.scale(scale, scale);
            bat.setColor(new Color(3, 7, 24));

            Path2D shape = new Path2D.Double();
            shape.moveTo(0, 10);
            shape.lineTo(-12, 0);
            shape.lineTo(-10, 13);
            shape.lineTo(-23, 7);
            shape.lineTo(-18, 22);
            shape.lineTo(-7, 20);
            shape.lineTo(0, 27);
            shape.lineTo(7, 20);
            shape.lineTo(18, 22);
            shape.lineTo(23, 7);
            shape.lineTo(10, 13);
            shape.lineTo(12, 0);
            shape.closePath();

            bat.fill(shape);
            bat.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherApp app = new WeatherApp();
            app.setVisible(true);
        });
    }
}