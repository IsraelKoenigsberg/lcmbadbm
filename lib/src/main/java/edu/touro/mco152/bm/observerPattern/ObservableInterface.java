package edu.touro.mco152.bm.observerPattern;

public interface ObservableInterface {
    void registerObserver(ObserverInterface observer);
    void unregisterObserver(ObserverInterface observer);
    void notifyObservers();
}
