package dev.lb.launchmaster;

import java.util.Map;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.Box;
import java.awt.CardLayout;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * A Launcher for subprograms with GUI and Paramters
 *
 * @author Lars Bündgen
 * @version 2.6
 */
public class LaunchMaster
{
    private JFrame frame;
    private JList<SubProgram> list;
    private JLabel desc;
    private Map<SubProgram, List<JComponent>> programme; 
    private JPanel optPanel;
    private CardLayout cards;
    private static final String widthTag = "<html><body style='width: 200px'>";

    private LaunchMaster(String titel){
        programme = new HashMap<>();
        cards = new CardLayout();
        //LAYOUT
        //Fenster
        frame = new JFrame("LaunchMaster - " + titel);
        //Content Pane
        JPanel cp = new JPanel();
        //Col1
        JLabel selectPrg = Utils.setSize(new JLabel("Select a program"), 210, 15);
        list = Utils.setSize(new JList<SubProgram>(new DefaultListModel<SubProgram>()), 210, 420);
        list.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5,5,5,5)));
        list.setMinimumSize(new Dimension(50,50));
        JButton start = Utils.setSize(new JButton("Starten"), 210, 30);
        //Col2
        JLabel prgDesc = Utils.setSize(new JLabel("Program description"), 250, 15);
        desc = Utils.setSize(new JLabel(widthTag + "No program selected"), 250, 420);
        desc.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5,5,5,5)));
        JButton exit = Utils.setSize(new JButton("Quit LaunchMaster"), 250, 30);
        //Col3
        JLabel startOpt = Utils.setSize(new JLabel("Launch options"), 480, 15);
        optPanel = new JPanel();
        optPanel.setLayout(cards);
        optPanel.add(new JLabel("No Program selected"), "%NULL%"); //Bitte kein Programm "%NULL%" nennen!!!
        JScrollPane jsp = Utils.setSize(new JScrollPane(optPanel), 480, 420);
        JButton lol = Utils.setSize(new JButton("Reset values"), 480, 30);

        for(JComponent c :  new JComponent[]{selectPrg, prgDesc, startOpt, list, desc, jsp, start, exit, lol}){
            cp.add(c);
        };
        Utils.makeCompactGrid(cp, 3, 3, 10, 10, 10, 10);

        //Ereignisse
        exit.addActionListener((k) -> System.exit(0));
        start.addActionListener((k) -> {
                SubProgram current = list.getSelectedValue();
                if(current == null){
                    JOptionPane.showMessageDialog(frame, "You have to select a program", "Launch", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                Object[] args = createArguments();
                if(args == null) return;
                Thread sub = new Thread(() -> current.startProgram(args));
                sub.start();
                updateUI();
            });
        list.addListSelectionListener((l) -> updateUI());
        lol.addActionListener((k) -> restoreDefaults(list.getSelectedValue()));
        
        
        frame.add(cp);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Object[] createArguments(){
        SubProgram current = list.getSelectedValue();
        if(current == null) return new Object[]{};
        Object[] args = new Object[current.getLaunchParameters().size()];
        for(int i = 0; i < current.getLaunchParameters().size(); i++){
            JComponent com = programme.get(current).get(i);
            Parameter par = current.getLaunchParameters().get(i);
            if(par.getParamType() == Parameter.Type.BOOLEAN && com instanceof JCheckBox){
                args[i] = ((JCheckBox) com).isSelected();
            }else if(par.getParamType() == Parameter.Type.FLOAT && com instanceof JSpinner){
                args[i] = (float) ((JSpinner) com).getValue();
            }else if(par.getParamType() == Parameter.Type.INT && com instanceof JSpinner){
                args[i] = (int) ((JSpinner) com).getValue();
            }else if(par.getParamType() == Parameter.Type.STRING && com instanceof JTextField){
                JTextField jtf = (JTextField) com;
                int min = Integer.parseInt(jtf.getName().split(";")[0]);
                int max = Integer.parseInt(jtf.getName().split(";")[1]);
                if(jtf.getText().length() < min || jtf.getText().length() > max){
                    JOptionPane.showMessageDialog(frame, "<html>The text in '" + par.getDescription() + "' does not fulfill these conditions: <br> Minimal length: " + min +
                        "<br> Maximal length: " + max, "Text length", JOptionPane.INFORMATION_MESSAGE);
                    return null;   
                }
                args[i] = jtf.getText();
            }else{
                System.err.println("No match");
                args[i] = null;
            }
        }
        return args;
    }

    private void updateUI(){
        SubProgram current = list.getSelectedValue();
        if(current == null){
            desc.setText(widthTag + "No program selected");
            cards.show(optPanel, "%NULL%");
            return;
        }else{
            desc.setText(widthTag + current.getDescription());
            cards.show(optPanel, current.getName());
            
        }
    }

    private void restoreDefaults(SubProgram current){
        if(current == null){
            JOptionPane.showMessageDialog(frame, "You have to select a program", "Reset", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for(int i = 0; i < current.getLaunchParameters().size(); i++){
            JComponent com = programme.get(current).get(i);
            Parameter par = current.getLaunchParameters().get(i);
            switch(par.getParamType()){
                case BOOLEAN: ((JCheckBox) com).setSelected((boolean) par.getDefaultValue()); break;
                case INT: ((JSpinner) com).setValue((int) (double) par.getDefaultValue()); break;
                case FLOAT: ((JSpinner) com).setValue((float)  (double) par.getDefaultValue()); break;
                case STRING: ((JTextField) com).setText((String) par.getDefaultValue()); break;
            }
        }
    }
    
    private List<JComponent> createCard(SubProgram sp){
        JPanel card = new JPanel();
        List<JComponent> coms = new ArrayList<>();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        for(Parameter p : sp.getLaunchParameters()){
            JPanel con = new JPanel();
            JLabel name = Utils.setSize(new JLabel(p.getDescription()), 240, 20);
            name.setToolTipText(p.getDescription());
            con.add(name);

            JComponent com = new JLabel("Oops"); //Error
            if(p.getParamType() == Parameter.Type.BOOLEAN){
                com = Utils.setSize(new JCheckBox("Add this parameter"), 200, 20);
            }else if(p.getParamType() == Parameter.Type.INT){
                int min = Double.isNaN(p.getMinimumValue()) ? Integer.MIN_VALUE : (int) p.getMinimumValue();
                int max = Double.isNaN(p.getMaximumValue()) ? Integer.MAX_VALUE : (int) p.getMaximumValue();
                com = Utils.setSize(new JSpinner(new SpinnerNumberModel(min, min ,max, 1)), 200, 20);
            }else if(p.getParamType() == Parameter.Type.FLOAT){
                double min = Double.isNaN(p.getMinimumValue()) ? Double.MIN_VALUE : (double) p.getMinimumValue();
                double max = Double.isNaN(p.getMaximumValue()) ? Double.MAX_VALUE : (double) p.getMaximumValue();
                com = Utils.setSize(new JSpinner(new SpinnerNumberModel(min, min ,max, 0.1)), 200, 20);
            }else if(p.getParamType() == Parameter.Type.STRING){
                com = Utils.setSize(new JTextField(), 200, 20);
                int min = 0;
                int max = 0;
                if(p.getMinimumValue() != Double.NaN && p.getMinimumValue() > 0){
                    min = (int) p.getMinimumValue();
                }
                if(p.getMaximumValue() != Double.NaN && p.getMaximumValue() >= min){
                    max = (int) p.getMaximumValue();
                }
                com.setName(min + ";" + max); //Wird spÃ¤ter noch schÃ¶ner gemacht
            }
            con.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
            con.add(com);
            coms.add(com);
            card.add(con);
        }
        card.setMinimumSize(new Dimension(450,coms.size()*20));
        card.add(Box.createGlue());
        optPanel.add(card, sp.getName());
        return coms;
    }

    public void addProgram(SubProgram sp){
        //Alle verÃ¤derungen an der Component-Struktur müssen anscheined auf den EDT
        SwingUtilities.invokeLater(() -> {
                programme.put(sp, createCard(sp));
                ((DefaultListModel<SubProgram>) list.getModel()).addElement(sp);
                restoreDefaults(sp);
                updateUI();
            });
    }

    public void addProgram(Class<?> clazz) throws Exception{
        addProgram(SubProgram.create(clazz));
    }
    
    /**
     * Creates a new LaunchMaster-Instance that searches the given package and its subpackages for programs
     * @param title The title of the LaunchMaster window
     * @param topLevelPackageName The full package name of the top level package of the search tree
     * @return The LaunchMaster instance
     */
    public static LaunchMaster create(String title, String topLevelPackageName) throws Exception{
        return create(title, topLevelPackageName, Thread.currentThread().getContextClassLoader());
    }
    
    /**
     * Creates a new LaunchMaster-Instance that searches the current package and its subpackages for programs
     * @param title The title of the LaunchMaster window
     * @deprecated This method uses the top package of the LaunchMaster class as root. Since this Library is intended to be used as
     * an API and not modified, there would be no classes in this package anyways 
     * @return The LaunchMaster instance
     */
    @Deprecated
    public static LaunchMaster create(String title) throws Exception{
        return LaunchMaster.create(title,null);
    }
    
    /**
     * Creates a new LaunchMaster-Instance that searches the given package and its subpackages for programs
     * @param title The title of the LaunchMaster window
     * @param topLevelPackageName The full package name of the top level package of the search tree
     * @param customcl The classloader to use for the search. The default one (for other methods) is Thread.currentThread().getContextClassLoader();
     * @return The LaunchMaster instance
     */
    public static LaunchMaster create(String title, String topLevelPackageName, ClassLoader customcl) throws Exception{
    	if(title == null) title = "Launch";
        if(topLevelPackageName == null){
            topLevelPackageName = LaunchMaster.class.getPackage().getName();
            if(topLevelPackageName.contains(".")){
            	topLevelPackageName = topLevelPackageName.substring(0, topLevelPackageName.indexOf('.'));
            }
        }
        LaunchMaster lm = new LaunchMaster(title);
        List<Class<?>> classes = Utils.getClassesForPackage(topLevelPackageName, customcl);
        for(Class<?> clazz : classes){
            if(clazz.isAnnotationPresent(Program.class)) lm.addProgram(clazz);
        }
        return lm;
    }
}
