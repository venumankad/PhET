// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public class Terrain {

    public final List<Float> xPositions = new ArrayList<Float>();
    public final List<Float> zPositions = new ArrayList<Float>();

    // indexed first by column (x), then by z
    private final List<List<TerrainSample>> samples = new ArrayList<List<TerrainSample>>();

    // nothing else besides elevation changed here (at least)
    public final ValueNotifier<Terrain> elevationChanged = new ValueNotifier<Terrain>( this );

    public final ValueNotifier<Terrain> columnsModified = new ValueNotifier<Terrain>( this );

    private final int zSamples;

    public Terrain( int zSamples, float initialMinZ, float initialMaxZ ) {
        this.zSamples = zSamples;
        for ( int i = 0; i < zSamples; i++ ) {
            float ratio = ( (float) ( i ) ) / ( (float) ( zSamples - 1 ) );
            zPositions.add( initialMinZ + ratio * ( initialMaxZ - initialMinZ ) );
        }
    }

    // the index that refers to the front of the terrain (where the cross-section is)
    public int getFrontZIndex() {
        return zSamples - 1;
    }

    public int getNumColumns() {
        return samples.size();
    }

    public int getNumRows() {
        return zSamples;
    }

    public TerrainSample getSample( int xIndex, int zIndex ) {
        return samples.get( xIndex ).get( zIndex );
    }

    public int getNumberOfVertices() {
        return getNumColumns() * getNumRows();
    }

    public List<TerrainSample> getColumn( int xIndex ) {
        return samples.get( xIndex );
    }

    public void addToLeft( float x, List<TerrainSample> column ) {
        assert column.size() == zSamples;
        samples.add( 0, column );
        xPositions.add( 0, x );
        columnsModified.updateListeners();
    }

    public void addToRight( float x, List<TerrainSample> column ) {
        assert column.size() == zSamples;
        samples.add( column );
        xPositions.add( x );
        columnsModified.updateListeners();
    }

    public void removeLeft() {
        samples.remove( 0 );
        columnsModified.updateListeners();
    }

    public void removeRight() {
        samples.remove( samples.size() - 1 );
        columnsModified.updateListeners();
    }

    // whether water should be displayed over this particular section of terrain
    public boolean hasWater() {
        return true;
    }

    public ImmutableVector2F[] getFrontVertices() {
        ImmutableVector2F[] result = new ImmutableVector2F[getNumColumns()];
        int zIndex = getFrontZIndex();
        for ( int xIndex = 0; xIndex < getNumColumns(); xIndex++ ) {
            result[xIndex] = new ImmutableVector2F( xPositions.get( xIndex ), getSample( xIndex, zIndex ).getElevation() );
        }
        return result;
    }

    public void setToFlatElevation( float elevation ) {
        for ( List<TerrainSample> list : samples ) {
            for ( TerrainSample point : list ) {
                point.setElevation( elevation );
            }
        }
    }

    public void shiftZ( float offset ) {
        for ( int i = 0; i < zPositions.size(); i++ ) {
            zPositions.set( i, zPositions.get( i ) + offset );
        }
    }

}
