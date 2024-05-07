package edu.touro.mco152.bm.observerPattern;

/**
 * Observable Pattern Interface. Allows for sibject classes to be observed and updating action
 * to be taken based on subjects' actions.
 */
public interface ObservableInterface {
    /**
     * Registers an observer to watch a subject class
     * @param observer
     */
    void registerObserver(ObserverInterface observer);

    /**
     * Unregisters an observer from watching a subject class
     * @param observer
     */
    void unregisterObserver(ObserverInterface observer);

    /**
     * Notifies all observers watching subject classes to update
     */
    void notifyObservers();
}
