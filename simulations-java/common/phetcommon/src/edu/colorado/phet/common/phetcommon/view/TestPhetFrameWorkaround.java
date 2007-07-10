package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * This class is an attempt to demonstrate the problem for which PhetFrameWorkaround is a workaround.
 * This version works correctly (i.e. doesn't exhibit the desired problem) on Windows Vista on 2.6 GHz x 2 Athlon processors.
 *
 */
public class TestPhetFrameWorkaround {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final PhetApplication phetApplication = new PhetApplication(new PhetApplicationConfig(args, new FrameSetup.CenteredWithSize(800, 600), PhetResources.forProject("phetcommon"))) {
                    protected PhetFrame createPhetFrame() {
                        return new PhetFrame(this);
//                        return new PhetFrameWorkaround(this);
                    }
                };
                final TestModule module = new TestModule("test", new SwingClock(30, 1));
                module.getClock().addClockListener(new ClockAdapter() {
                    public void clockTicked(ClockEvent clockEvent) {
                        module.contentPane.invalidate();
                        module.contentPane.revalidate();
                        module.contentPane.repaint();
                        module.contentPane.setText(System.currentTimeMillis() + " button time!");
                        module.contentPane.paintImmediately(0, 0, module.contentPane.getWidth(), module.contentPane.getHeight());
                    }
                });
                phetApplication.addModule(module);
                phetApplication.startApplication();

                module.contentPane.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Dialog dialog = new Dialog(phetApplication.getPhetFrame(), false);
                        dialog.setSize(400, 300);
                        Button comp = new Button("dialog button");

                        comp.setBackground(Color.green);
                        dialog.add(comp);
//                        dialog.pack();
                        dialog.setVisible(true);
                    }
                });
            }
        });

    }

    static class TestModule extends Module {
        private JButton contentPane;

        public TestModule(String name, IClock clock) {
            super(name, clock);
            contentPane = new JButton("Simulation Panel Button") {
                protected void paintComponent(Graphics g) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    super.paintComponent(g);
                }
            };
            setSimulationPanel(contentPane);
        }

    }
}


