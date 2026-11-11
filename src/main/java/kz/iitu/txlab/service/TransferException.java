package kz.iitu.txlab.service;

/** A CHECKED exception: by default Spring does NOT roll back a transaction because of it. */
public class TransferException extends Exception {

    public TransferException(String message) {
        super(message);
    }
}
