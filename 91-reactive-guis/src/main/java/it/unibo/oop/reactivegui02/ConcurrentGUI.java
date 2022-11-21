package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stopButton = new JButton("stop");
    private final JButton upButton = new JButton("up");
    private final JButton downButton = new JButton("down");

    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); 
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stopButton);
        panel.add(upButton);
        panel.add(downButton);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        new Thread(agent).start();
        /*
         * Register a listener that stops it
         */
        stopButton.addActionListener((e) -> agent.stopCounting());
        upButton.addActionListener((e) -> agent.increasing());
        downButton.addActionListener((e) -> agent.decreasing());
    }

    private class Agent implements Runnable {
        
        private volatile boolean stop;
        private boolean isIncreasing = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile 
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    this.counter += this.isIncreasing ? 1 : -1; //da verificare
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
            ConcurrentGUI.this.stopButton.setEnabled(false); //un modo assurdo di usare l'equivalente di "this" per indicare un campo dell'outer class
            ConcurrentGUI.this.upButton.setEnabled(false);
            ConcurrentGUI.this.downButton.setEnabled(false);
        }

        public void increasing() {
            this.isIncreasing = true;
        }

        public void decreasing() {
            this.isIncreasing = false;
        }
    }
}
