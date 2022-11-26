package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stopButton = new JButton("stop");
    private final JButton downButton = new JButton("down");
    private final JButton upButton = new JButton("up");

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) 
        (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(this.stopButton);
        panel.add(this.downButton);
        panel.add(this.upButton);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent1 = new Agent();
        new Thread(agent1).start();
        final StoppingAgent stoppingAgent = new StoppingAgent();
        new Thread(stoppingAgent).start();
        stopButton.addActionListener((e) -> agent1.stopCounting());
        downButton.addActionListener((e) -> agent1.decreasing());
        upButton.addActionListener((e) -> agent1.increasing());
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
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
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
            AnotherConcurrentGUI.this.stopButton.setEnabled(false); //un modo assurdo di usare l'equivalente di "this" per indicare un campo dell'outer class
            AnotherConcurrentGUI.this.upButton.setEnabled(false);
            AnotherConcurrentGUI.this.downButton.setEnabled(false);
        }

        public void increasing() {
            this.isIncreasing = true;
        }

        public void decreasing() {
            this.isIncreasing = false;
        }
    }

    private class StoppingAgent implements Runnable {

        private static final long SLEEP_QUANTITY = 1000*10L;

        @Override
        public void run() {
            try {
                Thread.sleep(SLEEP_QUANTITY);
                this.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void interrupt() {
            AnotherConcurrentGUI.this.stopButton.doClick();
        }
    }
}
