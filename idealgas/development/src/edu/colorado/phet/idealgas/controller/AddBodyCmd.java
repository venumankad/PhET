/**
 * Class: AddBodyCmd
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.physics.PhysicalSystem;

public class AddBodyCmd implements Command {
    private Body body;

    public AddBodyCmd( Body body ) {
        this.body = body;
    }

    public Object doIt() {
        PhysicalSystem.instance().addBody( body );
        return null;
    }
}
