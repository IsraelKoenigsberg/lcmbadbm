package edu.touro.mco152.bm.persist;

import edu.touro.mco152.bm.observerPattern.ObserverInterface;
import jakarta.persistence.EntityManager;

/**
 * Persistence data class. Observes appropriate subject classes to know when to act.
 */
public class DatabasePersistenceObserver implements ObserverInterface {
    @Override
    public void update(DiskRun run) {
          /*
           Persist info about the BM Run (e.g. into Derby Database)
          */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }
}
