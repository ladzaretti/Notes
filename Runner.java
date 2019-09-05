import javax.swing.*;

class Runner {
    public static void main(String[] args) {
        Object[] selection = new String[]{"New Calender", "Existing Calender"}; /*opening dialog selection options*/
        Object input; /*selected option*/
        /*show opening dialog, new/existing calender*/
        input = JOptionPane.showInputDialog(null, "Select your calender", "Open Calender", JOptionPane.PLAIN_MESSAGE, null, selection, selection[1]);
        if (input == null) System.exit(0);/*dialog closed by user, force exit*/
        if (input.toString().equals("New Calender")) { /*new cal*/
            new NotesUI(); /*empty constructor*/
        } else {/*existing cal*/
            JFileChooser fc = NotesUI.createNoteFileChooser();/*choose existing cal*/
            if ((fc.showOpenDialog(null)) == JFileChooser.APPROVE_OPTION)
                new NotesUI(fc.getSelectedFile());/*send opened file to constructor*/
        }
    }
}