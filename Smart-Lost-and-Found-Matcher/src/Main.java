import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame {

    private final java.util.List<LostItem> lostItems =
            new ArrayList<>();

    private final java.util.List<FoundItem> foundItems =
            new ArrayList<>();

    private final MatchingEngine engine =
            new MatchingEngine();

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel pageTitle;

    private JLabel lostCountLabel;
    private JLabel foundCountLabel;
    private JLabel matchCountLabel;

    private JTextField lostName;
    private JTextField lostLocation;
    private JTextArea lostDescription;
    private JComboBox<String> lostCategory;

    private JTextField foundName;
    private JTextField foundLocation;
    private JTextArea foundDescription;
    private JComboBox<String> foundCategory;

    private DefaultTableModel matchModel;
    private DefaultTableModel lostReportsModel;
    private DefaultTableModel foundReportsModel;

    private static final Color BG =
            new Color(15, 23, 42);

    private static final Color SIDEBAR =
            new Color(11, 18, 35);

    private static final Color PANEL =
            new Color(30, 41, 59);

    private static final Color ACCENT =
            new Color(59, 130, 246);

    private static final Color TEXT =
            new Color(241, 245, 249);

    private static final Color MUTED =
            new Color(148, 163, 184);

    public Main() {

        setTitle("Smart Lost & Found Matcher");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            lostItems.addAll(FileManager.loadLostItems());
            foundItems.addAll(FileManager.loadFoundItems());
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load saved reports: " + ex.getMessage()
            );
        }

        createUI();
    }

    private void createUI() {

        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 20, 15));

        JLabel logo = new JLabel("  FindIt");
        logo.setFont(new Font("SansSerif", Font.BOLD, 26));
        logo.setForeground(TEXT);

        JLabel subtitle = new JLabel("  Lost & Found System");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(35));

        sidebar.add(navButton("Dashboard", "dashboard"));
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Report Lost Item", "lost"));
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Report Found Item", "found"));
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Browse Lost Reports", "browseLost"));
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Browse Found Reports", "browseFound"));
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navButton("Find Matches", "matches"));

        add(sidebar, BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(25, 30, 20, 30));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 25));
        pageTitle.setForeground(TEXT);

        JLabel welcome = new JLabel("Smart Lost & Found Matcher");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 13));
        welcome.setForeground(MUTED);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(BG);

        heading.add(pageTitle);
        heading.add(Box.createVerticalStrut(5));
        heading.add(welcome);

        header.add(heading, BorderLayout.WEST);

        mainArea.add(header, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.setBorder(new EmptyBorder(0, 30, 30, 30));

        contentPanel.add(createDashboard(), "dashboard");
        contentPanel.add(createLostPage(), "lost");
        contentPanel.add(createFoundPage(), "found");
        contentPanel.add(createBrowsePage(true), "browseLost");
        contentPanel.add(createBrowsePage(false), "browseFound");
        contentPanel.add(createMatchesPage(), "matches");

        mainArea.add(contentPanel, BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);

        updateCounts();
        showPage("dashboard");
    }

    private JButton navButton(String title, String page) {

        JButton button = new JButton(title);

        button.setMaximumSize(new Dimension(200, 45));
        button.setPreferredSize(new Dimension(200, 45));

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));

        button.setForeground(TEXT);
        button.setBackground(SIDEBAR);

        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 10)
        );

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> showPage(page));

        return button;
    }

    private void showPage(String page) {

        cardLayout.show(contentPanel, page);

        switch (page) {

            case "dashboard":
                pageTitle.setText("Dashboard");
                updateCounts();
                break;

            case "lost":
                pageTitle.setText("Report Lost Item");
                break;

            case "found":
                pageTitle.setText("Report Found Item");
                break;

            case "browseLost":
                pageTitle.setText("All Lost Reports");
                refreshReports(true);
                break;

            case "browseFound":
                pageTitle.setText("All Found Reports");
                refreshReports(false);
                break;

            case "matches":
                pageTitle.setText("Matching Results");
                refreshMatches();
                break;
        }
    }

    private JPanel createDashboard() {

        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel intro = new JLabel(
                "Welcome! Manage lost and found belongings."
        );

        intro.setForeground(MUTED);
        intro.setFont(new Font("SansSerif", Font.PLAIN, 15));

        panel.add(intro);
        panel.add(Box.createVerticalStrut(25));

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 15));
        cards.setBackground(BG);

        lostCountLabel = new JLabel("0");
        foundCountLabel = new JLabel("0");
        matchCountLabel = new JLabel("0");

        cards.add(statCard("Lost Reports", lostCountLabel));
        cards.add(statCard("Found Reports", foundCountLabel));
        cards.add(statCard("Possible Matches", matchCountLabel));

        panel.add(cards);
        panel.add(Box.createVerticalStrut(35));

        JLabel heading = new JLabel("Quick Actions");
        heading.setForeground(TEXT);
        heading.setFont(new Font("SansSerif", Font.BOLD, 19));

        panel.add(heading);
        panel.add(Box.createVerticalStrut(15));

        JPanel actions = new JPanel(new GridLayout(2, 2, 15, 15));
        actions.setBackground(BG);

        JButton lost = actionButton("Report a Lost Item");
        lost.addActionListener(e -> showPage("lost"));

        JButton found = actionButton("Report a Found Item");
        found.addActionListener(e -> showPage("found"));

        JButton browse = actionButton("Browse All Reports");
        browse.addActionListener(e -> showPage("browseLost"));

        JButton matches = actionButton("Find Possible Matches");
        matches.addActionListener(e -> showPage("matches"));

        actions.add(lost);
        actions.add(found);
        actions.add(browse);
        actions.add(matches);

        panel.add(actions);

        return panel;
    }

    private JPanel statCard(String title, JLabel number) {

        JPanel card = new JPanel();
        card.setBackground(PANEL);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        number.setFont(new Font("SansSerif", Font.BOLD, 30));
        number.setForeground(ACCENT);

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(MUTED);

        card.add(number);
        card.add(Box.createVerticalStrut(8));
        card.add(label);

        return card;
    }

    private JPanel createLostPage() {

        lostName = new JTextField();
        lostLocation = new JTextField();
        lostDescription = new JTextArea(5, 20);
        lostCategory = categoryBox();

        return formPanel(
                "Enter details about the item you lost",
                "Item Name", lostName,
                "Category", lostCategory,
                "Last Seen Location", lostLocation,
                "Description", lostDescription,
                "Submit Lost Report",
                this::addLostItem
        );
    }

    private JPanel createFoundPage() {

        foundName = new JTextField();
        foundLocation = new JTextField();
        foundDescription = new JTextArea(5, 20);
        foundCategory = categoryBox();

        return formPanel(
                "Enter details about the item you found",
                "Item Name", foundName,
                "Category", foundCategory,
                "Found Location", foundLocation,
                "Description", foundDescription,
                "Submit Found Report",
                this::addFoundItem
        );
    }

    private JPanel formPanel(
            String info,
            String label1, JComponent field1,
            String label2, JComponent field2,
            String label3, JComponent field3,
            String label4, JComponent field4,
            String buttonText,
            Runnable action) {

        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel description = new JLabel(info);
        description.setForeground(MUTED);
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(description);
        panel.add(Box.createVerticalStrut(20));

        addField(panel, label1, field1);
        addField(panel, label2, field2);
        addField(panel, label3, field3);
        addField(panel, label4, field4);

        JButton submit = actionButton(buttonText);
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.addActionListener(e -> action.run());

        panel.add(Box.createVerticalStrut(15));
        panel.add(submit);

        return panel;
    }

    private void addField(
            JPanel panel,
            String label,
            JComponent field) {

        JLabel title = new JLabel(label);

        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (field instanceof JTextField) {
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setBackground(PANEL);
            field.setForeground(TEXT);
            field.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    new Color(71, 85, 105)
                            ),
                            new EmptyBorder(8, 10, 8, 10)
                    )
            );
        }

        if (field instanceof JComboBox) {
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setBackground(PANEL);
            field.setForeground(TEXT);
        }

        if (field instanceof JTextArea) {

            JTextArea area = (JTextArea) field;

            area.setFont(new Font("SansSerif", Font.PLAIN, 14));
            area.setBackground(PANEL);
            area.setForeground(TEXT);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setBorder(new EmptyBorder(8, 10, 8, 10));

            JScrollPane scroll = new JScrollPane(area);
            scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            panel.add(title);
            panel.add(Box.createVerticalStrut(7));
            panel.add(scroll);
            panel.add(Box.createVerticalStrut(15));

            return;
        }

        panel.add(title);
        panel.add(Box.createVerticalStrut(7));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private JComboBox<String> categoryBox() {

        String[] categories = {
                "Electronics",
                "Documents",
                "Wallet",
                "Bags",
                "Clothing",
                "Accessories",
                "Keys",
                "Other"
        };

        return new JComboBox<>(categories);
    }

    private JButton actionButton(String title) {

        JButton button = new JButton(title);

        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);

        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        );

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void addLostItem() {

        if (lostName.getText().isBlank()
                || lostLocation.getText().isBlank()
                || lostDescription.getText().isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields."
            );

            return;
        }

        LostItem item = new LostItem(
                lostName.getText().trim(),
                lostCategory.getSelectedItem().toString(),
                lostLocation.getText().trim(),
                lostDescription.getText().trim()
        );

        lostItems.add(item);

        try {

            FileManager.saveLostItems(lostItems);

            JOptionPane.showMessageDialog(
                    this,
                    "Lost report saved successfully!"
            );

            lostName.setText("");
            lostLocation.setText("");
            lostDescription.setText("");

            refreshReports(true);
            updateCounts();

        } catch (java.io.IOException ex) {

            lostItems.remove(item);

            JOptionPane.showMessageDialog(
                    this,
                    "Could not save report: " + ex.getMessage()
            );
        }
    }

    private void addFoundItem() {

        if (foundName.getText().isBlank()
                || foundLocation.getText().isBlank()
                || foundDescription.getText().isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields."
            );

            return;
        }

        FoundItem item = new FoundItem(
                foundName.getText().trim(),
                foundCategory.getSelectedItem().toString(),
                foundLocation.getText().trim(),
                foundDescription.getText().trim()
        );

        foundItems.add(item);

        try {

            FileManager.saveFoundItems(foundItems);

            JOptionPane.showMessageDialog(
                    this,
                    "Found report saved successfully!"
            );

            foundName.setText("");
            foundLocation.setText("");
            foundDescription.setText("");

            refreshReports(false);
            updateCounts();

        } catch (java.io.IOException ex) {

            foundItems.remove(item);

            JOptionPane.showMessageDialog(
                    this,
                    "Could not save report: " + ex.getMessage()
            );
        }
    }

    private JPanel createBrowsePage(boolean showLost) {

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(BG);

        JLabel info = new JLabel(
                showLost
                        ? "View all belongings reported as lost"
                        : "View all belongings reported as found"
        );

        info.setForeground(MUTED);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));

        String[] columns = {
                "Item Name",
                "Category",
                "Location",
                "Description"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JButton refresh = actionButton("Refresh Reports");
        refresh.addActionListener(e -> refreshReports(showLost));

        if (showLost) {
            lostReportsModel = model;
        } else {
            foundReportsModel = model;
        }

        panel.add(info, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshReports(boolean showLost) {

        DefaultTableModel model =
                showLost ? lostReportsModel : foundReportsModel;

        if (model == null) {
            return;
        }

        model.setRowCount(0);

        if (showLost) {

            for (LostItem item : lostItems) {

                model.addRow(new Object[]{
                        item.getName(),
                        item.getCategory(),
                        item.getLocation(),
                        item.getDescription()
                });
            }

        } else {

            for (FoundItem item : foundItems) {

                model.addRow(new Object[]{
                        item.getName(),
                        item.getCategory(),
                        item.getLocation(),
                        item.getDescription()
                });
            }
        }
    }

    private JPanel createMatchesPage() {

        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(BG);

        JLabel info = new JLabel(
                "Possible matches are ranked using item details."
        );

        info.setForeground(MUTED);
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));

        String[] columns = {
                "Lost Item",
                "Found Item",
                "Match Percentage"
        };

        matchModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(matchModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JButton refresh = actionButton("Find Matches Again");
        refresh.addActionListener(e -> refreshMatches());

        panel.add(info, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);

        return panel;
    }

    private void styleTable(JTable table) {

        table.setBackground(PANEL);
        table.setForeground(TEXT);

        table.setGridColor(new Color(51, 65, 85));
        table.setRowHeight(35);

        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setBackground(SIDEBAR);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        table.getTableHeader().setReorderingAllowed(false);
    }

    private void refreshMatches() {

        matchModel.setRowCount(0);

        java.util.List<String> matches =
                engine.findMatches(lostItems, foundItems);

        for (String result : matches) {

            String[] parts = result.split(" \\| Match: ");

            if (parts.length != 2) {
                continue;
            }

            String[] names = parts[0].split(" <-> ", 2);

            if (names.length == 2) {

                matchModel.addRow(new Object[]{
                        names[0],
                        names[1],
                        parts[1]
                });
            }
        }

        updateCounts();
    }

    private void updateCounts() {

        if (lostCountLabel != null) {
            lostCountLabel.setText(
                    String.valueOf(lostItems.size())
            );
        }

        if (foundCountLabel != null) {
            foundCountLabel.setText(
                    String.valueOf(foundItems.size())
            );
        }

        if (matchCountLabel != null) {

            int count = engine.findMatches(
                    lostItems,
                    foundItems
            ).size();

            matchCountLabel.setText(String.valueOf(count));
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}