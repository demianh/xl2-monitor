package ch.demianh.xl2monitor;

import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.prefs.Preferences;

public class App extends Frame implements ActionListener, WindowListener {

    private boolean TESTING_MODE = false;
    private String SYNC_URL = "http://mfw.usystems.ch/api.php";

    private Label dbValueLabel;
    private Label errorLabel;
    private Button stopBtn;
    private Button startBtn;
    private Button saveBtn;
    private TextField nameInput;
    private Preferences prefs;

    private Color headerBg = new Color((int)Long.parseLong("BCDBF5", 16));
    private Color inputBg = new Color((int)Long.parseLong("EDF4FA", 16));
    private Color defaultFg = new Color((int)Long.parseLong("032D52", 16));
    private Color errorFg = Color.RED;
    private Color inactiveFg = Color.GRAY;

    private Thread thread;
    private MonitorThread runnable = null;

    public static void main(String[] args){
        App app = new App(args);
        app.setSize(300,300);
        app.setVisible(true);
        app.setResizable(false);
    }

    private App(String[] args) {

        int paramNo = 0;
        for (String arg: args) {
            // first cli param is testing mode (true|{anything})
            if(paramNo == 0){
                if(arg.equals("true")){
                    System.out.println("Starting in Testing Mode");
                    this.TESTING_MODE = true;
                }
            }
            // second cli param is custom sync url (http url)
            if(paramNo == 1){
                if(arg.startsWith("http")){
                    System.out.println("Using custom Sync URL: " + arg);
                    this.SYNC_URL = arg;
                }
            }
            paramNo++;
        }

        this.setTitle("XL2 Monitor");
        this.setLayout(null);

        // ----- Fonts & Colors -----

        Font robotoThin = null;
        Font robotoRegular = null;
        try {
            robotoThin = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Roboto-Thin.ttf")).deriveFont(12f);
            robotoRegular = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Roboto-Regular.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Roboto-Thin.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Roboto-Regular.ttf")));
        } catch (Exception e) {
            System.out.println("Font could not be loaded");
        }


        Font defaultFont = new Font(robotoThin.getName(), Font.PLAIN, 16);
        Font largeFont = new Font(robotoThin.getName(), Font.PLAIN, 70);
        Font buttonFont = new Font(robotoRegular.getName(), Font.PLAIN, 14);
        Font errorFont = new Font(robotoRegular.getName(), Font.PLAIN, 14);

        this.setBackground(Color.WHITE);


        // ----- Header -----

        Label nameLabel = new Label("Name");
        nameLabel.setSize(60, 30);
        nameLabel.setLocation(10, 34);
        nameLabel.setBackground(headerBg);
        nameLabel.setForeground(defaultFg);
        nameLabel.setFont(defaultFont);
        this.add(nameLabel);

        nameInput = new TextField(this.getName(), 20);
        nameInput.setBounds(70,30,120,30);
        nameInput.setVisible(true);
        nameInput.setBackground(inputBg);
        nameInput.setForeground(defaultFg);
        nameInput.setFont(defaultFont);
        this.add(nameInput);

        saveBtn = new Button("Speichern");
        this.add(saveBtn);
        saveBtn.setBounds(200,27,95,36);
        saveBtn.setBackground(headerBg);
        saveBtn.addActionListener(this);

        Label header = new Label("");
        header.setSize(300, 70);
        header.setLocation(0, 0);
        header.setBackground(headerBg);
        this.add(header);


        // ----- dB View -----

        Label dblabel = new Label("dB");
        dblabel.setFont(largeFont);
        dblabel.setForeground(defaultFg);
        dblabel.setBounds(10,100,100,100);
        this.add(dblabel);

        dbValueLabel = new Label("--.-");
        dbValueLabel.setFont(largeFont);
        dbValueLabel.setForeground(inactiveFg);
        dbValueLabel.setBounds(110,100,300,100);
        this.add(dbValueLabel);


        // ----- Controls -----

        errorLabel = new Label("");
        errorLabel.setForeground(errorFg);
        errorLabel.setBounds(12,220,300,30);
        errorLabel.setFont(errorFont);
        this.add(errorLabel);

        startBtn = new Button("Start");
        this.add(startBtn);
        startBtn.setBounds(10,250,100,40);
        startBtn.setEnabled(true);
        startBtn.setFont(buttonFont);
        startBtn.addActionListener(this);

        stopBtn = new Button("Stop");
        stopBtn.setBounds(110,250,100,40);
        this.add(stopBtn);
        stopBtn.setEnabled(false);
        stopBtn.setFont(buttonFont);
        stopBtn.addActionListener(this);


        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.addWindowListener(this);

        this.startMonitoring();
    }

    private void startMonitoring(){
        if(thread != null){
            stopMonitoring();
        }

        runnable = new MonitorThread(this, TESTING_MODE, this.SYNC_URL);
        thread = new Thread(runnable);
        thread.start();
        setStateStarted();
        setErrorMessage("");
    }

    private void stopMonitoring(){
        setStateStopped();
        if(thread != null){
            thread.interrupt();
            thread = null;
        }
        runnable = null;
    }

    private void saveName(){
        if(this.prefs == null){
            this.prefs = Preferences.userRoot().node(this.getClass().getName());
        }
        this.prefs.put("NAME", nameInput.getText());
    }

    public String getName(){
        if(this.prefs == null){
            this.prefs = Preferences.userRoot().node(this.getClass().getName());
        }
        return this.prefs.get("NAME", "Name");
    }

    public void setErrorMessage(String message){
        this.errorLabel.setText(message);
    }

    public void setValue(Double value){
        this.dbValueLabel.setText(Double.toString(value));
        if(value >= 100){
            this.dbValueLabel.setForeground(errorFg);
        } else if(value <= 0) {
            this.dbValueLabel.setForeground(inactiveFg);
        } else {
            this.dbValueLabel.setForeground(defaultFg);
        }
    }

    public void setStateStarted(){
        stopBtn.setEnabled(true);
        startBtn.setEnabled(false);
    }

    public void setStateStopped(){
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public void paint(Graphics g){}
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == stopBtn){
            this.stopMonitoring();
        }

        if(e.getSource() == startBtn){
            this.startMonitoring();
        }

        if(e.getSource() == saveBtn){
            this.saveName();
        }
    }
    public void windowClosing(WindowEvent event) {
        this.stopMonitoring();
        System.exit(0);
    }
    public void windowIconified(WindowEvent event) { }
    public void windowOpened(WindowEvent event) { }
    public void windowClosed(WindowEvent event) { }
    public void windowActivated(WindowEvent event) { }
    public void windowDeiconified(WindowEvent event) { }
    public void windowDeactivated(WindowEvent event) { }
}