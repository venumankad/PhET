/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 4:16:28 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.graphics;

import edu.colorado.phet.physics.PhysicalSystem;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Observable;

public class ClockPanelLarge extends JPanel {

    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat = NumberFormat.getInstance();

    public ClockPanelLarge() {

        setBackground( new Color( 237, 225, 113 ));
        setBorder( BorderFactory.createRaisedBevelBorder() );
        clockTF = new JTextField( 8 );
        Font clockFont = clockTF.getFont();
        clockTF.setFont( new Font( clockFont.getName(), Font.BOLD, 16 ));

        add( new JLabel( "Running time: "));
        clockTF.setEditable( false );
        add( clockTF );
        clockFormat.setMaximumFractionDigits( 1 );
    }

    /**
     *
     */
    public void setClockReading( String reading ) {
        clockTF.setText( reading );
    }

    public void setClockReading( float  reading ) {
        setClockReading( clockFormat.format( reading ));
    }

    public void update( Observable o, Object arg ) {
        setClockReading( PhysicalSystem.instance().getSystemClock().getRunningTime() );
    }

    /**
     *
     */
    public void setClockPanelVisible( boolean isVisible ) {
        setVisible( isVisible );
    }

    /**
     *
     */
    public boolean isClockPanelVisible() {
        return isVisible();
    }

}
