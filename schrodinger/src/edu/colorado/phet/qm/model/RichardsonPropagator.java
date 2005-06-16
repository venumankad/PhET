package edu.colorado.phet.qm.model;


/*********************************************************/
/* Two-dimensional Time dependent Schrodinger Equation.  */
/* Use Crank-Nicholson/Cayley algorithm...               */
/* Stable, Norm Conserving.     Li Ju. May.3,1995        */

/**
 * *****************************************************
 */

public class RichardsonPropagator implements Propagator {
    private double simulationTime;

    private double deltaTime;
    private int timeStep;

    private BoundaryCondition boundaryCondition;
    private Potential potential;

    double hbar, mass, epsilon;
    private Complex alpha;
    private Complex beta;
    private Complex[][] betaeven;
    private Complex[][] betaodd;
//    public double dt;
    public Complex[][] copy;

    public RichardsonPropagator( double TAU, BoundaryCondition boundaryCondition, Potential potential ) {
        this.deltaTime = TAU;
        this.boundaryCondition = boundaryCondition;
        this.potential = potential;
        simulationTime = 0.0;
        timeStep = 0;
        hbar = 1;
        mass = 1;//0.0020;

        deltaTime = 0.8 * mass / hbar;

        update();
    }

    private void update() {
        epsilon = toEpsilon( deltaTime );

//        alpha = new Complex( ( 1 + Math.cos( epsilon ) ) / 2.0, -Math.sin( epsilon ) / 2.0 );
//        beta = new Complex( ( 1 - Math.cos( epsilon ) ) / 2.0, Math.sin( epsilon ) / 2.0 );

        alpha = new Complex( 0.5 + 0.5 * Math.cos( epsilon / 2 ), -0.5 * Math.sin( epsilon / 2 ) );
        beta = new Complex( ( Math.sin( epsilon / 4 ) ) * Math.sin( epsilon / 4 ), 0.5 * Math.sin( epsilon / 2 ) );
        System.out.println( "deltaTime= " + deltaTime );
        System.out.println( "epsilon = " + epsilon );
        System.out.println( "alpha = " + alpha );
        System.out.println( "beta = " + beta );
    }

    private double toEpsilon( double dt ) {
        return hbar * dt / ( mass );
    }

    public void propagate( Complex[][] w ) {
        int nx = w.length;
//        double norm = new ProbabilityValue().compute( w );
//        System.out.println( "pre: norm = " + norm );
        betaeven = new Complex[nx][nx];
        betaodd = new Complex[nx][nx];
        for( int i = 0; i < betaeven.length; i++ ) {
            for( int j = 0; j < betaeven[i].length; j++ ) {
                betaeven[i][j] = new Complex();
                betaodd[i][j] = new Complex();
                if( ( i + j ) % 2 == 0 ) {
                    betaeven[i][j] = beta;
                }
                else {
                    betaodd[i][j] = beta;
                }
            }
        }
        prop2D( w );
//        norm = new ProbabilityValue().compute( w );
//        System.out.println( "post: norm = " + norm );
        simulationTime += deltaTime;
        timeStep++;
    }

    private void prop2D( Complex[][] w ) {
        copy = Wavefunction.copy( w );
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
        stepIt( w, 1, 0 );
        stepIt( w, -1, 0 );
        applyPotential( w );
        stepIt( w, -1, 0 );
        stepIt( w, 1, 0 );
        stepIt( w, 0, 1 );
        stepIt( w, 0, -1 );
    }

    private void stepIt( Complex[][] w, int dx, int dy ) {
        Wavefunction.copy( w, copy );
        for( int i = 1; i < w.length - 1; i++ ) {
            for( int j = 1; j < w[0].length - 1; j++ ) {
                Complex alphaTerm = alpha.times( copy[i][j] );
                Complex evenTerm = betaeven[i][j].times( copy[i + dx][j + dy] );
                Complex oddTerm = betaodd[i][j].times( copy[i - dx][j - dy] );
                w[i][j].setValue( alphaTerm.plus( evenTerm ).plus( oddTerm ) );
            }
        }
    }

    private void applyPotential( Complex[][] w ) {
        for( int i = 1; i < w.length - 1; i++ ) {
            for( int j = 1; j < w[0].length - 1; j++ ) {
                double pot = potential.getPotential( i, j, timeStep );
                Complex val = new Complex( Math.cos( pot * deltaTime / hbar ), -Math.sin( pot * deltaTime / hbar ) );
                w[i][j] = w[i][j].times( val );
            }
        }
    }

    public void setDeltaTime( double deltaTime ) {
        this.deltaTime = deltaTime;
        update();
    }

    public double getSimulationTime() {
        return simulationTime;
    }
}