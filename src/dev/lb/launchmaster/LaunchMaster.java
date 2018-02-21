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
import javax.swing.BoxLayout;
import java.awt.Dimension;
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
        frame.setMinimumSize(new Dimension(500, 200));
        //Content Pane
        JPanel cp = new JPanel();
        //Col1
        JLabel selectPrg = Utils.setSize(new JLabel("Select a program"), 210, 15);
        list = Utils.setSize(new JList<SubProgram>(new DefaultListModel<SubProgram>()), 210, 420);
        list.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5,5,5,5)));
        list.setMinimumSize(new Dimension(50,50));
        JButton start = Utils.setSize(new JButton("Launch"), 210, 30);
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
        JButton reset = Utils.setSize(new JButton("Reset values"), 480, 30);

        for(JComponent c :  new JComponent[]{selectPrg, prgDesc, startOpt, list, desc, jsp, start, exit, reset}){
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
                if(!current.updateUI(true)){
                	return;
                }
                Object[] args;
				try {
					args = createArguments();
				} catch (ValueOutOfRangeException e) {
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Range", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try{
					Thread sub = new Thread(() -> current.startProgram(args));
	                sub.start();
				}catch(Exception e){
					JOptionPane.showMessageDialog(frame, "Error while launching", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
                updateUI();
            });
        list.addListSelectionListener((l) -> updateUI());
        reset.addActionListener((k) -> restoreDefaults(list.getSelectedValue()));
        
        
        frame.add(cp);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Object[] createArguments() throws ValueOutOfRangeException{
        SubProgram current = list.getSelectedValue();
        if(current == null) return new Object[]{};
        Object[] args = new Object[current.getLaunchParameters().size()];
        for(int i = 0; i < current.getLaunchParameters().size(); i++){
            args[i] = current.getLaunchParameters().get(i).readValue();
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
        
    }
    
    private List<JComponent> createCard(SubProgram sp){
        JPanel card = new JPanel();
        List<JComponent> coms = new ArrayList<>();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        for(Parameter p : sp.getLaunchParameters()){
            JComponent con = p.createComponent(new Dimension(240, 20), new Dimension(180, 20));
            card.add(con);
        }
        card.setMinimumSize(new Dimension(450,coms.size()*20));
        card.add(Box.createGlue());
        optPanel.add(card, sp.getName());
        sp.updateUI(false);
        return coms;
    }

    protected void addProgram(SubProgram sp){
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
     * @deprecated Because searching for classes is too buggy (does not work on some IDEs or compiled)
     * @return The LaunchMaster instance
     */
    @Deprecated
    public static LaunchMaster create(String title, String topLevelPackageName) throws Exception{
        return create(title, topLevelPackageName, Thread.currentThread().getContextClassLoader());
    }
    
    /**
     * Creates a new LaunchMaster-Instance that does not search for classes.
     * @param title The title of the LaunchMaster window
     * @return The LaunchMaster instance
     */
    public static LaunchMaster create(String title){
        return new LaunchMaster(title);
    }
    
    /**
     * Creates a new LaunchMaster-Instance that searches the given package and its subpackages for programs
     * @param title The title of the LaunchMaster window
     * @param topLevelPackageName The full package name of the top level package of the search tree
     * @param customcl The classloader to use for the search. The default one (for other methods) is Thread.currentThread().getContextClassLoader();
     * @deprecated Because searching for classes is too buggy (does not work on some IDEs or compiled)
     * @return The LaunchMaster instance
     */
    @Deprecated
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
