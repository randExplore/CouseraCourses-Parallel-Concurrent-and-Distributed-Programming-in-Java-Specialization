package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        SieveActorActor actor = new SieveActorActor(2);
        finish(() -> {
                for (int i = 3; i<=limit; i+=2) {
                    actor.send(i);
                }
        });

        int cnt = 0;
        SieveActorActor cur = actor;
        while (cur != null) {
            cnt += cur.numPrimes;
            cur = cur.nextActor;
        }
        return cnt;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        final private int size = 100;
        private int[] localPrimes = new int[size];
        private int numPrimes = 0;
        public SieveActorActor nextActor;

        public SieveActorActor(int initialPrime) {
            localPrimes[0] = initialPrime;
            numPrimes++;
        }

        private boolean checkPrime(int num) {
            for (int i = 0; i < numPrimes; i++) {
                if (num % localPrimes[i] == 0) return false;
            }
            return true;
        }

        @Override
        public void process(final Object msg) {
            final int num = (Integer) msg;
            boolean isPrime = checkPrime(num);
            if (isPrime) {
                if (numPrimes < size) {
                    localPrimes[numPrimes] = num;
                    numPrimes++;
                }
                else {
                    if (nextActor == null) {
                        nextActor = new SieveActorActor(num);
                        nextActor.send(msg);
                    }
                    else {
                        nextActor.send(msg);
                    }
                }
            }
        }
    }
}
