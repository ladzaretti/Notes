import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

/*the following class creates the main UI.*/
class NotesUI extends JFrame implements ActionListener {
    private HashMap<Date, String> hash;/*hashmap, key = date. value = string note.*/
    private File file = null; /*file containing the hashmap serialization*/
    private JTextArea noteText; /*text area to display the corresponding note.*/
    private DateJCB datePanel; /*panel containing the three Jcomboboxes representing chosen the date*/
    private SaveLoadNote notePanel;

    /*static method. returns file chooser for *.note files*/
    static JFileChooser createNoteFileChooser() {
        JFileChooser fc = new JFileChooser(Paths.get("").toAbsolutePath().toString());/*create new FC in the current working dir*/
        FileNameExtensionFilter noteFilter = new FileNameExtensionFilter("notes files (*.note)", "note"); /*create *.note file filter*/
        /*set file filter to *.note*/
        fc.addChoosableFileFilter(noteFilter);
        fc.setFileFilter(noteFilter);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);/*set only files to be chosen*/
        fc.setMultiSelectionEnabled(false); /*single file only.*/
        return fc;
    }

    /*noteUI initialization sub method*/
    private void init() {
        datePanel = new DateJCB();
        noteText = new JTextArea();
        notePanel = new SaveLoadNote();
        this.setTitle("Notes");
        this.setLayout(new BorderLayout());
        this.add(datePanel, BorderLayout.NORTH);
        this.add(notePanel, BorderLayout.SOUTH);
        JScrollPane scroll = new JScrollPane(noteText); /*set note area as scrollable*/
        scroll.setViewportView(noteText);
        scroll.setPreferredSize(new Dimension(400, 200));
        this.add(scroll, BorderLayout.CENTER);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Object[] selection = new String[]{"Save changes", "Discard changes"}; /*opening dialog selection options*/
                Object input; /*selected option*/
                /*show exit dialog*/
                input = JOptionPane.showInputDialog(null, "Options:", "Save current calendar", JOptionPane.PLAIN_MESSAGE, null, selection, selection[0]);
                if (input != null) {/*selection made*/
                    if (input.toString().equals("Save changes"))/*save calendar*/
                        NotesUI.this.writeObject();
                    NotesUI.this.exitUI();/*exit program*/
                }
            }
        });
    }

    private void exitUI() {
        this.dispose();
        System.exit(0);
    }

    /*noteUI constructor on new calendar*/
    NotesUI() {
        hash = new HashMap<>();
        init();
    }

    /*noteUI constructor on existing calendar*/
    NotesUI(File hashFile) {
        try {
            file = hashFile;
            readObject(hashFile);
        } catch (IOException e) {
            System.out.println("IO error");
            System.exit(1);
        }
        init();
        noteText.setText(hash.get(datePanel.getDate())); /*display note of the default showing date*/
    }

    @SuppressWarnings("unchecked cast")
    /*serial read object of type hashMap<date,string>, set instance variable hash as read hashmap*/
    private void readObject(File hashFile) throws IOException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(hashFile));
            hash = (HashMap<Date, String>) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
            System.exit(1);
        }
    }

    /*the following method receives a string representation a file name.
     * returns a string with the given file name with .note extension.*/
    private String createNoteFileExtension(String orgName) {
        StringBuilder path = new StringBuilder(orgName);
        int index = path.lastIndexOf(".");/*get extension index*/
        if (index > 0) {/*if there is an extension*/
            if (!path.substring(index).equals(".note"))/*check if .note*/
                path.replace(index, path.length(), ".note"); /*if isnt, overwrite with .note*/
        } else if (index < 0) {/*has no extension, add .note*/
            path.append(".note");
        }
        return path.toString();
    }

    /*serial writeObject of type hashMap<date,string>*/
    private void writeObject() {
        JFileChooser fc = NotesUI.createNoteFileChooser();/*create file chooser for *.note*/
        if (file == null) {/*if current instance has no file open, show save dialog*/
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                String noteFile = createNoteFileExtension(file.getName());
                file = new File(file.getParent(), noteFile);
            } else return; /*FileChooser closed, didn't get any file. abort save*/
        }
        /*if opened new file, serial save the hashmap to it*/
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(hash);
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }

    /*sub panel class containing the date selection comboboxes*/
    class DateJCB extends JPanel {
        private final JComboBox<Integer> day;
        private final JComboBox<Integer> month;
        private final JComboBox<Integer> year;

        /*panel constructor*/
        DateJCB() {
            this.setLayout(new GridLayout(1, 3));
            day = new JComboBox<>(range(1, 31));/*set day combobox range*/
            month = new JComboBox<>(range(1, 12));/*set month combobox range*/
            year = new JComboBox<>(range(2010, 2028));/*set year combobox range*/
            add(day);
            add(month);
            add(year);
            /*set noteUI as action listener for the above.*/
            day.addActionListener(NotesUI.this);
            month.addActionListener(NotesUI.this);
            year.addActionListener(NotesUI.this);
        }

        /*private sub method that returns an array of integers with incrementing values from
         * given input start to end.*/
        private Integer[] range(int start, int end) {
            int size = end - start + 1;
            Integer[] res = new Integer[size];
            for (int i = 0; i < size; i++)
                res[i] = i + start;
            return res;
        }

        /*returns the current chosen date*/
        Date getDate() {
            return new Date((Integer) day.getSelectedItem(), (Integer) month.getSelectedItem(), (Integer) year.getSelectedItem());
        }
    }

    /*sub panel containing the save note/load note buttons*/
    class SaveLoadNote extends JPanel {
        private final JButton save;
        private final JButton load;

        SaveLoadNote() {
            save = new JButton("Save Note");
            load = new JButton("Load Note");
            save.addActionListener(NotesUI.this);
            load.addActionListener(NotesUI.this);
            this.setLayout(new GridLayout(1, 2));
            this.add(save);
            this.add(load);
        }
    }

    /*action listener*/
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        /*if save/load button pressed*/
        if (obj instanceof JButton) {
            if (obj == notePanel.load)
                /*load note to display*/
                noteText.setText(hash.get(datePanel.getDate()));
            else if (obj == notePanel.save) {
                hash.put(datePanel.getDate(), noteText.getText()); /*save into hashMap*/
                /*writeObject(); save to file is done on exit.*/
            }
        } else if (obj instanceof JComboBox)/*if changed date via combobox, display the proper note to screen*/
            //noteText.setText(hash.get(datePanel.getDate()));
            noteText.setText("");
    }
}
