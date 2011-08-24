// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author John Blanco
 */
public class AttachedState extends BiomoleculeBehaviorState {

    private static final Random RAND = new Random();
    private static final Double MIN_ATTACHMENT_TIME = 0.5; // Seconds.

    private final AttachmentSite attachmentSite;

    private double attachmentCountdownTime;

    public AttachedState( AttachmentSite attachmentSite ) {
        this.attachmentSite = attachmentSite;
        // Calculate the attachment time.  The time is based on the affinity of
        // the attachment site for this molecule.
        if ( attachmentSite.getAffinity() == 1 ) {
            // Attach forever.
            attachmentCountdownTime = Double.POSITIVE_INFINITY;
        }
        else {
            // TODO: This is a total guess, and probably will need tweaking.
            attachmentCountdownTime = MIN_ATTACHMENT_TIME + 10 * attachmentSite.getAffinity() + ( 1 - RAND.nextDouble() ) * 5;
        }
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {
        if ( biomolecule.getPosition().distance( attachmentSite.locationProperty.get() ) > 0.0001 ) {
            // The attachment site appears to have moved, so follow it.
            biomolecule.setPosition( attachmentSite.locationProperty.get() );
        }
        attachmentCountdownTime -= dt;
        if ( attachmentCountdownTime <= 0 ) {
            // Time to fall off of this attachment site.
            attachmentSite.inUse.set( false );
            if ( attachmentSite.locationProperty.get().getY() < 100 ) {
                // Must be on the DNA, so drift upwards.
                return new DetachingState( new ImmutableVector2D( 0, 1 ) );
            }
            else {
                // Not on the DNA - drift randomly.
                return new DetachingState();
            }
        }
        else {
            // No state change.
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( List<AttachmentSite> proposedAttachmentSites, final MobileBiomolecule biomolecule ) {
        List<AttachmentSite> copyOfProposedAttachmentSites = new ArrayList<AttachmentSite>( proposedAttachmentSites );
        for ( AttachmentSite attachmentSite : proposedAttachmentSites ) {
            if ( attachmentSite.equals( this.attachmentSite ) ) {
                // Remove the one to which we are currently attached from the
                // list of sites to consider.
                copyOfProposedAttachmentSites.remove( attachmentSite );
            }
        }
        if ( copyOfProposedAttachmentSites.size() > 0 ) {
            if ( RAND.nextDouble() > 0.9 ) {
                // Sort the proposals.
                Collections.sort( copyOfProposedAttachmentSites, new Comparator<AttachmentSite>() {
                    public int compare( AttachmentSite as1, AttachmentSite as2 ) {
                        return Double.compare( Math.pow( biomolecule.getPosition().distance( as1.locationProperty.get() ), 2 ) * as1.getAffinity(),
                                               Math.pow( biomolecule.getPosition().distance( as2.locationProperty.get() ), 2 ) * as2.getAffinity() );
                    }
                } );
                int newAttachmentSiteIndex = 0;
                if ( copyOfProposedAttachmentSites.size() >= 2 ) {
                    // Choose randomly between the first two on the list.
                    newAttachmentSiteIndex = RAND.nextInt( 2 );
                }
                // Accept the first one on the list.
                return new MovingTowardsAttachmentState( copyOfProposedAttachmentSites.get( newAttachmentSiteIndex ) );
            }
        }
        // If we make it to here, just stay in the current state.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        attachmentSite.inUse.set( false );
        return new UnattachedAndAvailableState();
    }
}
